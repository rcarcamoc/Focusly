# RFP y Guía Técnica: Desarrollo de Aplicación Android "Time-Lapse" (Nombre Provisional)
**Documento Versión:** 2.0
**Fecha:** 02 de Julio de 2025

---

## 1. Resumen del Proyecto

### 1.1. Introducción
Se solicita el desarrollo de una aplicación nativa para Android, denominada provisionalmente "Time-Lapse". El propósito principal de la aplicación es medir y mostrar al usuario el tiempo transcurrido entre los eventos de bloqueo y desbloqueo de su dispositivo. La aplicación debe ser ligera, eficiente en el consumo de batería y centrarse en una experiencia de usuario minimalista, clara y útil.

### 1.2. Objetivo del MVP (Producto Mínimo Viable)
El MVP debe cumplir de forma robusta con las siguientes funcionalidades:
*   **Medición Precisa de Tiempo:** Registrar el intervalo de tiempo exacto entre que el teléfono se bloquea (`ACTION_SCREEN_OFF`) y se desbloquea (`ACTION_USER_PRESENT`).
*   **Notificación Instantánea y Discreta:** Al desbloquear el dispositivo, mostrar un mensaje conciso (ej. un "Toast" o una superposición no intrusiva) que informe el tiempo que el teléfono estuvo inactivo.
*   **Gestión de Permisos Robusta:** Asegurar un funcionamiento continuo en segundo plano. La app debe detectar, solicitar y guiar al usuario para conceder los permisos necesarios, especialmente aquellos para evitar las optimizaciones de batería que puedan detener el servicio.
*   **Pantalla de Estado/Inicio:** Una interfaz principal que sirva como un "dashboard", informando al usuario de un vistazo si la aplicación funciona correctamente, si faltan permisos y ofreciendo accesos directos para solucionar cualquier problema.
*   **Historial Mínimo (Retención):** Incluir una sección o vista en el MVP que muestre los últimos 5 intervalos de tiempo registrados. Esto proporciona una sensación de progreso y valor a largo plazo para el usuario, fomentando la retención.

### 1.3. Objetivos a Largo Plazo (Post-MVP)
*   **Análisis por Aplicación:** Explorar la viabilidad de medir el tiempo entre usos de aplicaciones específicas (ej. Instagram, TikTok).
*   **Mensajes Personalizados y Dinámicos:** Integrar una API para mostrar mensajes, citas o datos curiosos aleatorios relacionados con el tiempo de inactividad, con un sistema de caché local para no repetir contenido y optimizar el rendimiento.
*   **Estadísticas y Visualización:** Ofrecer al usuario un historial completo y gráficos sobre sus patrones de uso y desuso del dispositivo.

---

## 2. Especificaciones Técnicas Detalladas

### 2.1. Arquitectura y Stack Tecnológico
*   **Lenguaje:** **Kotlin**. Es el lenguaje preferido y oficial para el desarrollo nativo de Android.
*   **Arquitectura:** **MVVM (Model-View-ViewModel)**. Utilizar los Componentes de Arquitectura de Android (ViewModel, LiveData/Flow, Room, Lifecycle) para una base de código moderna, escalable y mantenible.
*   **Asincronía:** **Coroutines de Kotlin**. Para gestionar todas las operaciones en segundo plano (I/O, cálculos) de forma eficiente y sin bloquear el hilo principal.
*   **Inyección de Dependencias:** **Hilt (o Dagger 2)**. Para desacoplar el código y facilitar las pruebas y el mantenimiento.
*   **Base de Datos Local:** **Room**. Para el almacenamiento persistente de registros de tiempo, configuraciones de usuario y el caché de mensajes/citas.

### 2.2. Funcionalidad Principal: Detección de Bloqueo/Desbloqueo
Este es el núcleo de la aplicación.
*   **Implementación:**
    *   Se debe utilizar un `Foreground Service` para garantizar que el proceso no sea eliminado por el sistema. Este servicio será el responsable de registrar y mantener vivo un `BroadcastReceiver`.
    *   El `BroadcastReceiver` debe escuchar dos acciones del sistema: `Intent.ACTION_SCREEN_OFF` (para registrar el timestamp de inicio) y `Intent.ACTION_USER_PRESENT` (para registrar el timestamp final, calcular la diferencia y mostrar el resultado).
*   **Ejemplo de Código (Conceptual para el BroadcastReceiver):**
    ```kotlin
    class LockUnlockReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Inyectar un Repositorio para manejar la lógica de datos
            val dataRepository = DataRepository(context)

            when (intent.action) {
                Intent.ACTION_SCREEN_OFF -> {
                    dataRepository.saveLockTimestamp(System.currentTimeMillis())
                }
                Intent.ACTION_USER_PRESENT -> {
                    val lockTime = dataRepository.getLockTimestamp()
                    if (lockTime > 0) {
                        val elapsedTime = System.currentTimeMillis() - lockTime
                        val formattedTime = formatElapsedTime(elapsedTime) // Ej: "2h 15m 30s"
                        
                        // Mostrar resultado vía Toast
                        Toast.makeText(context, "Tiempo inactivo: $formattedTime", Toast.LENGTH_LONG).show()
                        
                        // Limpiar el timestamp para el próximo ciclo
                        dataRepository.clearLockTimestamp()
                        // Opcional: Guardar el registro en Room para estadísticas
                        dataRepository.logSession(startTime = lockTime, duration = elapsedTime)
                    }
                }
            }
        }
    }
    ```

### 2.3. Servicio en Segundo Plano y Gestión de Permisos
La estabilidad del servicio es crítica.
*   **Foreground Service:** El servicio debe ejecutarse en primer plano, mostrando una notificación persistente pero discreta. La notificación debe ser de baja prioridad, con un ícono simple y un texto claro como "Time-Lapse está protegiendo tu tiempo".
*   **Permisos Esenciales:**
    1.  `FOREGROUND_SERVICE`: Para ejecutar el servicio. En Android 14+, especificar el tipo (ej. `FOREGROUND_SERVICE_DATA_SYNC`).
    2.  `RECEIVE_BOOT_COMPLETED`: Para reiniciar el servicio automáticamente si el dispositivo se reinicia.
    3.  `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`: El más importante. La app debe detectar si no tiene este permiso y guiar activamente al usuario para que la añada a la lista de "no optimizadas".
    4.  `POST_NOTIFICATIONS` (Android 13+): Necesario para la notificación del servicio.
*   **Flujo de Usuario para Permisos:** La pantalla principal debe ser el centro de control. Si un permiso falta, debe mostrar una tarjeta o un aviso claro con un botón de "Solucionar" que lleve al usuario directamente a la pantalla de ajustes del sistema correspondiente.

### 2.4. Plan de Error y Recuperación ante Bloqueos del Sistema
Es crucial que la aplicación sea resiliente a interrupciones inesperadas y bloqueos del sistema.
*   **Detección de Fallos:** La aplicación debe implementar mecanismos para detectar si el `Foreground Service` ha sido terminado inesperadamente por el sistema (ej. por falta de memoria, optimizaciones agresivas del fabricante). Esto puede hacerse mediante la verificación periódica del estado del servicio al reabrir la app o escuchando `ACTION_MY_PACKAGE_REPLACED`.
*   **Registro de Errores (Local):** Implementar un sistema de logging local (ej. usando `Timber` o un logger simple en Room) para registrar eventos importantes, advertencias y errores. Esto facilitará la depuración y el entendimiento de los fallos en diferentes dispositivos y versiones de Android.
*   **Auto-Recuperación:**
    *   Si el servicio se detiene, la app debe intentar reiniciarlo automáticamente (siempre que los permisos lo permitan). Esto es especialmente relevante después de un `BOOT_COMPLETED`.
    *   El dashboard debe reflejar claramente si el servicio no está activo y ofrecer una opción manual para reiniciarlo.
*   **Notificación al Usuario:** En caso de fallos persistentes o que requieran intervención del usuario (ej. si el sistema mata el servicio repetidamente a pesar de los permisos), la app debe notificar al usuario a través del dashboard o una notificación no intrusiva, explicando la situación y ofreciendo posibles soluciones o un enlace a una sección de ayuda.

---

## 3. Guía de Diseño de Interfaz y Experiencia de Usuario (UI/UX)

El diseño debe ser limpio, moderno y funcional, siguiendo los lineamientos de Material Design 3.

### 3.1. Pantallas de la Aplicación

#### Pantalla Principal (Dashboard)
*   **Propósito:** Informar el estado y guiar al usuario.
*   **Componentes:**
    *   **Indicador de Estado Visual:** Un elemento central grande (ej. un círculo) que cambia de color:
        *   **Verde:** Todo funciona correctamente.
        *   **Naranja/Amarillo:** Se requiere atención (falta un permiso, servicio inactivo).
        *   **Rojo:** Error crítico o servicio detenido por el usuario.
    *   **Texto de Estado:** Un mensaje claro debajo del indicador: "Servicio activo", "Se requiere permiso de batería", "Servicio detenido", "Error: Reiniciar app".
    *   **Botón de Acción Contextual:** Si se requiere una acción, un botón prominente como "Conceder Permiso" o "Reiniciar Servicio".
    *   **Último Intervalo:** Un pequeño recuadro mostrando el último tiempo de inactividad medido.
    *   **Acceso a Ajustes:** Un ícono de engranaje para configuraciones avanzadas.

#### Historial Mínimo (MVP)
*   **Propósito:** Mostrar una sensación de progreso y valor a largo plazo, fomentando la retención.
*   **Componentes:** Una lista simple y desplazable que muestre los últimos 5 intervalos de tiempo registrados, con la fecha y duración de cada uno. Se puede implementar como una sección dentro del dashboard o una vista separada de fácil acceso.

#### Pantalla de Estadísticas (Opcional para MVP)
*   **Propósito:** Visualizar patrones de uso.
*   **Componentes:**
    *   **Gráfico de Barras:** Tiempo total de inactividad por día (últimos 7 días).
    *   **KPIs (Indicadores Clave):** Promedio diario, récord de inactividad, número de desbloqueos.
    *   **Historial Detallado:** Una lista desplazable de todas las sesiones registradas.

### 3.2. Iconografía y Estilo Visual
*   **Icono de la App:** Un diseño memorable y relacionado con el tiempo, la concentración o el bienestar digital. **Evitar connotaciones con fotografía o video.** Ejemplos conceptuales: un reloj de arena estilizado, un círculo con un símbolo de pausa, un ojo cerrado, un cerebro meditando, un árbol creciendo. El nombre final de la app influirá en el diseño del icono.
*   **Paleta de Colores:** Se recomienda un modo oscuro por defecto para ser agradable a la vista y ahorrar batería en pantallas OLED. Usar un color de acento vibrante (ej. verde esmeralda, azul eléctrico, púrpura) para los elementos interactivos y estados positivos.
*   **Tipografía:** Una fuente sans-serif moderna y legible como `Roboto`, `Inter` o `Poppins`.

### 3.3. Naming y Branding (Consideraciones)
*   El nombre actual "Time-Lapse" puede generar confusión con aplicaciones de fotografía/video.
*   **Se recomienda encarecidamente explorar nombres que evoquen los conceptos de "tiempo protegido", "hábitos digitales", "concentración", "desconexión", "bienestar" o "presencia".**
*   **Ejemplos de Nombres Conceptuales:** FocusPulse, Inactivo, PauseMind, TiempoFuera, ZenTime, ScreenBreak, Unplugged, MindfulTime, DigitalPause.
*   El nombre final debe ser único, fácil de recordar y reflejar el propósito principal de la aplicación, evitando ambigüedades.

---

## 4. Funcionalidades Opcionales y Evolución Futura

Estas funcionalidades se desarrollarán en fases posteriores, pero la arquitectura inicial debe facilitar su futura integración.

### 4.1. Análisis por Aplicación (Post-MVP)
*   **Implementación:** Utilizar el `UsageStatsManager` de Android.
*   **Permiso Requerido:** `PACKAGE_USAGE_STATS`. Es un permiso sensible que requiere una guía clara para el usuario.
*   **Funcionalidad:** Permitir al usuario seleccionar una o más apps y medir el tiempo transcurrido desde que se cerró hasta que se volvió a abrir.

### 4.2. Mensajes Aleatorios (Post-MVP)
*   **Implementación:** Conectar a una API de citas o frases (ej. `ZenQuotes.io`, `API Ninjas`).
*   **Estrategia de Caché:** Descargar un lote de frases (ej. 50-100) y almacenarlas en la base de datos Room. Usar las frases del caché local para no depender de la red en cada desbloqueo. Cuando el stock de frases no usadas sea bajo, descargar un nuevo lote en segundo plano (idealmente con Wi-Fi).

### 4.3. Evolución Recomendada: Cronómetro en Pantalla de Bloqueo (Función Avanzada)
*   **Concepto:** Mostrar un contador de tiempo en vivo en la pantalla de bloqueo o en el Always-On Display (AOD).
*   **Análisis:**
    *   **Ventajas:** Alta visibilidad, experiencia de usuario "premium", refuerzo del hábito de no usar el móvil.
    *   **Desafíos:** Alto consumo de batería si se actualiza por segundo, gran complejidad técnica y problemas de compatibilidad entre fabricantes (Samsung, Xiaomi, etc.).
*   **Recomendación:** **No incluir en el MVP.** Abordar esta función en una fase posterior como una característica "Pro" o "Experimental". La implementación más viable podría ser a través de un `Live Wallpaper` para la pantalla de bloqueo, con una configuración para actualizar el contador por minuto (en lugar de por segundo) para mitigar el consumo de batería.

---

## 5. Consideraciones de Privacidad y Confianza

La privacidad del usuario es una prioridad fundamental para esta aplicación. Se debe comunicar de forma clara y proactiva la política de datos para generar confianza.
*   **No Recolección de Datos Sensibles:** La aplicación **no recolectará, transmitirá ni compartirá ningún dato personal o sensible** del usuario a servidores externos. Todos los datos de uso (tiempos de inactividad, historial) se procesarán y almacenarán **exclusivamente en el dispositivo del usuario**.
*   **Transparencia y Comunicación:** La aplicación debe incluir una sección de "Privacidad" en los ajustes o en la pantalla de bienvenida, con un lenguaje directo y fácil de entender, que reafirme esta política (ej. "Tus datos son tuyos. Todo queda en tu dispositivo. No recolectamos información personal.").
*   **Permisos Mínimos:** Solo se solicitarán los permisos estrictamente necesarios para el funcionamiento de la aplicación. Cada permiso solicitado debe estar justificado claramente al usuario, explicando por qué es necesario.

---

## 6. Entregables Esperados
1.  **Código Fuente Completo:** Proyecto de Android Studio (Kotlin) bien documentado, siguiendo las mejores prácticas y la arquitectura definida. Incluirá tests unitarios y de integración cuando sea apropiado.
2.  **APK Firmado de Release:** El archivo `.apk` o `.aab` listo para ser publicado en la Google Play Store.
3.  **Documentación del Proyecto:** Un archivo `README.md` detallado explicando la arquitectura, las decisiones técnicas clave, el plan de recuperación ante fallos, y las instrucciones para compilar y ejecutar el proyecto. Incluirá un diagrama de arquitectura de alto nivel.
4.  **Recursos de Diseño:** Archivos fuente (ej. SVG, Figma) para el icono de la app y otros elementos gráficos creados.

## 7. Criterios de Evaluación de la Propuesta
Las propuestas serán evaluadas considerando:
*   **Experiencia y Portafolio:** Experiencia demostrable en desarrollo nativo de Android con Kotlin y apps publicadas, especialmente aquellas que manejen servicios en segundo plano, gestión de batería y privacidad de datos.
*   **Comprensión del Proyecto:** Claridad en la propuesta sobre cómo se abordarán los desafíos técnicos (estabilidad del servicio, gestión de permisos, plan de recuperación) y la visión de privacidad y experiencia de usuario.
*   **Plan de Trabajo:** Un cronograma de desarrollo detallado con hitos claros (ej. Semana 1: Setup y servicio base; Semana 2: UI y permisos; etc.), incluyendo fases de prueba y un plan para la gestión de la calidad.
*   **Costo y Plazos:** Una estimación transparente del costo total y el tiempo de entrega para el MVP, con flexibilidad para futuras fases.



### 4.4. Modo Concentración (Post-MVP2)
*   **Objetivo:** Permitir al usuario iniciar voluntariamente un período de concentración, motivándolo a no usar el dispositivo y reforzando el hábito mediante visuales, mensajes y recompensas.
*   **Flujo General del Usuario:**
    1.  El usuario abre la app y pulsa "Iniciar concentración". Puede elegir un tiempo predefinido (ej. 25m, 45m, 90m) o personalizado.
    2.  Se inicia una cuenta regresiva con una animación visual (círculo de progreso o barra) y una frase motivacional en pantalla.
    3.  **(Opcional)** Si el usuario lo permite, se activará el modo silencio para las notificaciones durante la sesión.
    4.  Si el usuario desbloquea el teléfono antes de tiempo, se le muestra un mensaje de interrupción (ej. "Aún quedan X minutos. ¿Quieres continuar?").
    5.  Al finalizar el tiempo sin interrupciones, se muestra un mensaje motivador o una "recompensa visual" (tipo logro).
*   **Componentes Técnicos:**
    *   **Pantalla "Modo Concentración":** Incluirá un selector de tiempo, un checkbox para "Silenciar notificaciones durante la sesión" y un botón "Iniciar concentración".
    *   **Servicio en Segundo Plano:** Un temporizador robusto (ej. usando `CountDownTimer` o `Coroutine` con `delay`) para la cuenta regresiva. Registrará el inicio y fin de la sesión. Si el usuario desbloquea el teléfono, se activará una `Activity` de interrupción. El estado de la sesión debe persistir en Room.
    *   **Silencio de Notificaciones (Opcional):** Uso de `NotificationManager` y el permiso `ACCESS_NOTIFICATION_POLICY`. Se requiere una guía clara al usuario para conceder este acceso. El método a utilizar sería `notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)`.
    *   **Mensajes Motivacionales:** Reutilizar el caché local de frases. Se puede mostrar una frase diferente cada cierto intervalo (ej. 10 minutos). Al finalizar una sesión exitosa, se mostrará un mensaje especial de felicitación (ej. "¡25 minutos de foco total! Tu atención vale oro.").
*   **UI/UX Sugerido:**
    *   **Pantalla de Sesión Activa:** Temporizador grande al centro (mm:ss), frase del momento debajo, fondo sobrio (modo oscuro, sin estímulos). Botón "Finalizar antes de tiempo" (con doble confirmación).
    *   **Pantalla de Interrupción:** Se muestra si el usuario intenta usar el celular. Mensaje: "Aún no termina tu tiempo de concentración. ¿Te rendirás ahora?". Opciones: "Continuar" o "Finalizar sesión" (requiere justificar o tocar dos veces).
*   **Consideraciones:**
    *   **Permisos:** `ACCESS_NOTIFICATION_POLICY` (para silenciar notificaciones).
    *   **Compatibilidad:** Validar el comportamiento en diferentes versiones de Android (especialmente Android 12+) y fabricantes.
    *   **Persistencia:** La sesión de concentración debe restaurarse si el usuario reinicia el dispositivo.
    *   **Privacidad:** No recolectar información sobre qué aplicaciones abre o no el usuario durante la sesión, solo la duración del enfoque.
*   **Bonus: Funciones Opcionales a Futuro:**
    *   Integración con sonido ambiente (foco, lluvia, música instrumental).
    *   Widget en pantalla principal.
    *   Historial detallado de sesiones exitosas.
    *   Gamificación: subir de nivel, desbloquear frases especiales o logros.

