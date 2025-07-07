# Plan de Implementación - Estadísticas y Visualización Focusly

**Versión:** 1.0  
**Fecha:** 06 de Julio de 2025  
**Objetivo:** Implementar sistema completo de estadísticas y visualización para reforzar el cambio de comportamiento

---

## 🎯 Funcionalidades a Implementar

### 1. Estadísticas Integradas en Dashboard
- [ ] Nueva sección de estadísticas en pantalla principal
- [ ] Acceso directo desde el dashboard
- [ ] Vista rápida de KPIs principales

### 2. Gráficos de Análisis (Últimos 7 días)

#### 2.1 Gráfico de Barras - Tiempo Promedio entre Desbloqueos
- **Objetivo:** Mostrar el tiempo promedio de inactividad por día
- **Métrica:** Promedio de duración de sesiones por día
- **Visualización:** Barras verticales con colores por día
- **Interactividad:** Tap para ver detalles del día

#### 2.2 Gráfico de Líneas - Cantidad de Desbloqueos
- **Objetivo:** Mostrar la frecuencia de uso del dispositivo
- **Métrica:** Número total de desbloqueos por día
- **Visualización:** Línea temporal con puntos de datos
- **Interactividad:** Zoom y pan para explorar tendencias

#### 2.3 Gráfico de Dispersión - Bloques Horarios vs Desbloqueos
- **Objetivo:** Identificar patrones de uso por hora del día
- **Métrica:** Cantidad de desbloqueos por bloque horario (6h, 12h, 18h, 24h)
- **Visualización:** Scatter plot con puntos de diferentes tamaños
- **Interactividad:** Filtros por día de la semana

### 3. KPIs Principales
- [ ] **Promedio diario:** Tiempo promedio de inactividad por día
- [ ] **Récord:** Sesión más larga registrada
- [ ] **Número de desbloqueos:** Total de desbloqueos en el período
- [ ] **Tendencia:** Comparación con período anterior

### 4. Historial Completo
- [ ] Lista scrollable de todas las sesiones
- [ ] Filtros por fecha y duración
- [ ] Búsqueda y ordenamiento
- [ ] Exportación de datos

---

## 🏗️ Arquitectura Técnica

### Stack de Visualización
- **Biblioteca:** MPAndroidChart (más estable y completa para Android)
- **Alternativa:** Victory Native (si prefieres React Native charts)
- **Backup:** Canvas personalizado con Compose

### Estructura de Datos
```kotlin
// Nuevas entidades para estadísticas
data class DailyStats(
    val date: LocalDate,
    val totalSessions: Int,
    val averageDuration: Long,
    val totalUnlocks: Int,
    val longestSession: Long
)

data class HourlyStats(
    val hour: Int,
    val unlockCount: Int,
    val averageDuration: Long
)
```

### Nuevos DAOs
```kotlin
@Dao
interface StatsDao {
    @Query("SELECT * FROM sessions WHERE date(createdAt/1000, 'unixepoch') = :date")
    fun getSessionsByDate(date: String): Flow<List<Session>>
    
    @Query("""
        SELECT 
            date(createdAt/1000, 'unixepoch') as date,
            COUNT(*) as totalSessions,
            AVG(duration) as averageDuration,
            COUNT(*) as totalUnlocks,
            MAX(duration) as longestSession
        FROM sessions 
        WHERE createdAt >= :startDate 
        GROUP BY date(createdAt/1000, 'unixepoch')
        ORDER BY date DESC
    """)
    fun getDailyStats(startDate: Long): Flow<List<DailyStats>>
    
    @Query("""
        SELECT 
            CAST(strftime('%H', datetime(createdAt/1000, 'unixepoch')) AS INTEGER) as hour,
            COUNT(*) as unlockCount,
            AVG(duration) as averageDuration
        FROM sessions 
        WHERE createdAt >= :startDate 
        GROUP BY hour
        ORDER BY hour
    """)
    fun getHourlyStats(startDate: Long): Flow<List<HourlyStats>>
}
```

---

## 📱 UI/UX Design

### Pantalla de Estadísticas
```
┌─────────────────────────────────┐
│ ← Estadísticas          📊     │
├─────────────────────────────────┤
│ KPIs Principales               │
│ ┌─────┐ ┌─────┐ ┌─────┐       │
│ │Prom │ │Récord│ │Total│       │
│ │2h15m│ │4h30m│ │ 45  │       │
│ └─────┘ └─────┘ └─────┘       │
├─────────────────────────────────┤
│ Gráficos (Tabs)               │
│ [Barras] [Líneas] [Dispersión]│
│ ┌─────────────────────────────┐ │
│ │                             │ │
│ │     Gráfico Activo          │ │
│ │                             │ │
│ └─────────────────────────────┘ │
├─────────────────────────────────┤
│ Historial Completo             │
│ [Ver Todo] [Exportar]         │
└─────────────────────────────────┘
```

### Interacciones
- **Tap en gráfico:** Ver detalles del día/hora
- **Swipe:** Navegar entre diferentes gráficos
- **Pull to refresh:** Actualizar datos
- **Long press:** Opciones adicionales

---

## 🧪 Testing Strategy

### Tests Unitarios
- [ ] `StatsDaoTest`: Tests para consultas de estadísticas
- [ ] `StatsRepositoryTest`: Tests para lógica de negocio
- [ ] `ChartDataTest`: Tests para transformación de datos

### Tests de UI
- [ ] `StatisticsScreenTest`: Tests de composables
- [ ] `ChartInteractionTest`: Tests de interacciones
- [ ] `PerformanceTest`: Tests de rendimiento con muchos datos

---

## 📊 Métricas de Éxito

### Técnicas
- [ ] **Rendimiento:** Gráficos se cargan en <2 segundos
- [ ] **Memoria:** Uso <50MB adicionales
- [ ] **Batería:** Impacto <5% en consumo

### UX
- [ ] **Engagement:** Usuarios revisan estadísticas 3+ veces por semana
- [ ] **Retención:** 80% de usuarios activos después de 2 semanas
- [ ] **Feedback:** 4.5+ estrellas en reviews

---

## 🚀 Plan de Implementación

### Sprint 1: Base de Estadísticas (3-4 días)
1. **Día 1-2:** Implementar DAOs y Repository para estadísticas
2. **Día 3:** Crear ViewModel para estadísticas
3. **Día 4:** UI básica de estadísticas

### Sprint 2: Gráficos (4-5 días)
1. **Día 1-2:** Integrar MPAndroidChart
2. **Día 3:** Implementar gráfico de barras
3. **Día 4:** Implementar gráfico de líneas
4. **Día 5:** Implementar gráfico de dispersión

### Sprint 3: KPIs e Historial (2-3 días)
1. **Día 1:** Implementar KPIs principales
2. **Día 2:** Historial completo con filtros
3. **Día 3:** Testing y optimización

### Sprint 4: Pulido y Testing (2 días)
1. **Día 1:** Testing completo
2. **Día 2:** Optimización de rendimiento

---

## 📦 Dependencias a Agregar

```kotlin
// MPAndroidChart para gráficos
implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

// Date/Time utilities
implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")

// Math utilities para cálculos estadísticos
implementation("org.apache.commons:commons-math3:3.6.1")
```

---

## 🎨 Consideraciones de Diseño

### Paleta de Colores
- **Verde:** Sesiones largas (>2h)
- **Naranja:** Sesiones medias (30m-2h)
- **Rojo:** Sesiones cortas (<30m)
- **Azul:** Línea de tendencia

### Accesibilidad
- [ ] Soporte para lectores de pantalla
- [ ] Alto contraste
- [ ] Tamaños de texto configurables
- [ ] Navegación por teclado

---

## 🔮 Roadmap Post-Estadísticas

### Funcionalidades Futuras
- [ ] **Exportación:** CSV/PDF de estadísticas
- [ ] **Compartir:** Screenshots de logros
- [ ] **Notificaciones:** Recordatorios de metas
- [ ] **Gamificación:** Logros y badges
- [ ] **Comparación:** Con períodos anteriores
- [ ] **Predicciones:** IA para predecir patrones

---

¿Te parece bien este plan? ¿Quieres que comience con la implementación del Sprint 1 (Base de Estadísticas)? 