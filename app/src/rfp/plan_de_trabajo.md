# Plan de Trabajo - MVP Focusly

**Versión:** 1.0  
**Fecha:** 02 de Julio de 2025  
**Objetivo:** Implementar el MVP de la aplicación Focusly según especificaciones del RFP

---

## Resumen del MVP

### Funcionalidades Core
1. **Medición Precisa de Tiempo:** Registrar intervalo entre bloqueo (`ACTION_SCREEN_OFF`) y desbloqueo (`ACTION_USER_PRESENT`)
2. **Notificación Instantánea:** Mostrar tiempo de inactividad al desbloquear
3. **Gestión de Permisos Robusta:** Manejo completo de permisos necesarios
4. **Dashboard Principal:** Pantalla de estado y control
5. **Historial Mínimo:** Últimos 5 intervalos registrados

### Stack Tecnológico
- **Lenguaje:** Kotlin
- **Arquitectura:** MVVM con Componentes de Arquitectura Android
- **Base de Datos:** Room
- **Inyección de Dependencias:** Hilt
- **Asincronía:** Coroutines

---

## Hitos de Implementación

### Hito 1: Configuración Inicial y Arquitectura Base
**Duración:** 1-2 días  
**Objetivos:**
- Configurar proyecto Android con Kotlin
- Implementar arquitectura MVVM base
- Configurar Hilt para inyección de dependencias
- Crear estructura de paquetes
- Configurar Room para base de datos

**Entregables:**
- Proyecto base compilando correctamente
- Estructura de paquetes organizada
- Dependencias configuradas en build.gradle
- Clases base de arquitectura (ViewModel, Repository, etc.)

### Hito 2: Base de Datos y Entidades
**Duración:** 1 día  
**Objetivos:**
- Diseñar esquema de base de datos
- Implementar entidades Room
- Crear DAOs para acceso a datos
- Configurar migraciones de base de datos

**Entregables:**
- Entidad Session para almacenar intervalos
- DAO para operaciones CRUD
- Base de datos configurada
- Tests unitarios básicos para DAO

### Hito 3: Servicio en Segundo Plano
**Duración:** 2-3 días  
**Objetivos:**
- Implementar Foreground Service
- Crear BroadcastReceiver para detectar bloqueo/desbloqueo
- Implementar lógica de medición de tiempo
- Configurar notificación del servicio

**Entregables:**
- ForegroundService funcional
- LockUnlockReceiver detectando eventos
- Lógica de cálculo de tiempo implementada
- Servicio persistente en segundo plano

### Hito 4: Gestión de Permisos
**Duración:** 1-2 días  
**Objetivos:**
- Implementar detección de permisos
- Crear flujo de solicitud de permisos
- Manejar permisos críticos (batería, notificaciones)
- Implementar guía de usuario para permisos

**Entregables:**
- Sistema de verificación de permisos
- Flujo de solicitud de permisos
- Manejo de permisos de batería
- Tests de permisos

### Hito 5: UI Principal - Dashboard
**Duración:** 2-3 días  
**Objetivos:**
- Diseñar e implementar pantalla principal
- Crear indicadores de estado visual
- Implementar botones de acción contextual
- Mostrar último intervalo medido

**Entregables:**
- MainActivity con diseño Material Design 3
- Indicadores de estado (verde/naranja/rojo)
- Botones de acción funcionales
- Integración con ViewModel

### Hito 6: Historial y Notificaciones
**Duración:** 1-2 días  
**Objetivos:**
- Implementar vista de historial (últimos 5 registros)
- Crear sistema de notificaciones Toast
- Integrar historial con base de datos
- Implementar formateo de tiempo

**Entregables:**
- Vista de historial funcional
- Notificaciones Toast al desbloquear
- Formateo de tiempo legible
- Tests de integración

### Hito 7: Plan de Recuperación y Robustez
**Duración:** 2 días  
**Objetivos:**
- Implementar detección de fallos del servicio
- Crear sistema de auto-recuperación
- Implementar logging local
- Manejar reinicio automático del servicio

**Entregables:**
- Sistema de detección de fallos
- Auto-recuperación del servicio
- Logging con Timber
- Manejo de BOOT_COMPLETED

### Hito 8: Testing y Pulido Final
**Duración:** 1-2 días  
**Objetivos:**
- Implementar tests unitarios completos
- Realizar tests de integración
- Optimizar rendimiento y consumo de batería
- Pulir UI/UX final

**Entregables:**
- Tests unitarios >80% cobertura
- Tests de integración
- APK optimizado
- Documentación final

---

## Cronograma Total

**Duración Estimada:** 11-17 días  
**Fechas Tentativas:**
- Hito 1-2: Días 1-3
- Hito 3-4: Días 4-8  
- Hito 5-6: Días 9-13
- Hito 7-8: Días 14-17

---

## Criterios de Éxito por Hito

### Hito 1: Configuración Inicial
- [ ] Proyecto compila sin errores
- [ ] Hilt configurado correctamente
- [ ] Estructura de paquetes implementada
- [ ] Dependencias agregadas al build.gradle

### Hito 2: Base de Datos
- [ ] Room configurado y funcionando
- [ ] Entidades creadas y validadas
- [ ] DAOs implementados
- [ ] Tests unitarios pasando

### Hito 3: Servicio en Segundo Plano
- [ ] ForegroundService ejecutándose
- [ ] BroadcastReceiver detectando eventos
- [ ] Cálculo de tiempo funcionando
- [ ] Notificación del servicio visible

### Hito 4: Gestión de Permisos
- [ ] Detección de permisos implementada
- [ ] Flujo de solicitud funcionando
- [ ] Permisos de batería manejados
- [ ] Guía de usuario implementada

### Hito 5: Dashboard
- [ ] UI principal implementada
- [ ] Indicadores de estado funcionando
- [ ] Botones de acción operativos
- [ ] Integración con ViewModel

### Hito 6: Historial y Notificaciones
- [ ] Vista de historial implementada
- [ ] Notificaciones Toast funcionando
- [ ] Formateo de tiempo correcto
- [ ] Datos persistidos en Room

### Hito 7: Recuperación
- [ ] Detección de fallos implementada
- [ ] Auto-recuperación funcionando
- [ ] Logging configurado
- [ ] Reinicio automático operativo

### Hito 8: Testing y Pulido
- [ ] Tests unitarios >80% cobertura
- [ ] Tests de integración pasando
- [ ] APK optimizado
- [ ] Documentación completa

---

## Riesgos y Mitigaciones

### Riesgos Técnicos
1. **Compatibilidad con diferentes versiones de Android**
   - *Mitigación:* Testing en múltiples versiones, uso de APIs compatibles

2. **Optimizaciones agresivas de fabricantes**
   - *Mitigación:* Implementar múltiples estrategias de persistencia

3. **Permisos complejos en Android 13+**
   - *Mitigación:* Guía detallada de usuario y fallbacks

### Riesgos de Tiempo
1. **Complejidad del servicio en segundo plano**
   - *Mitigación:* Prototipado temprano, testing continuo

2. **Gestión de permisos más compleja de lo esperado**
   - *Mitigación:* Investigación previa, documentación detallada

---

## Entregables Finales

1. **Código Fuente Completo:** Proyecto Android Studio bien documentado
2. **APK Firmado:** Listo para distribución
3. **Documentación:** README.md con arquitectura y instrucciones
4. **Tests:** Suite completa de tests unitarios e integración
5. **Recursos:** Iconos y assets de la aplicación

---

## Notas de Implementación

- Cada hito debe compilar y funcionar correctamente antes de avanzar
- Implementar tests unitarios desde el inicio
- Documentar decisiones técnicas importantes
- Mantener código limpio y bien comentado
- Seguir las mejores prácticas de Android y Kotlin 