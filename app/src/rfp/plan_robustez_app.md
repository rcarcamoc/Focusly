# Plan de Implementación - Robustez contra Cierres Accidentales

**Versión:** 1.0  
**Fecha:** 06 de Julio de 2025  
**Prioridad:** CRÍTICA - Antes de estadísticas  
**Objetivo:** Garantizar que Focusly funcione continuamente incluso si el usuario cierra la app

---

## 🚨 Problema Identificado

### Escenarios de Cierre Accidental:
1. **Usuario cierra la app** desde el gestor de aplicaciones
2. **Sistema mata la app** por falta de memoria
3. **Optimizaciones agresivas** del fabricante (Xiaomi, Huawei, etc.)
4. **Reinicio del dispositivo** sin que la app se reinicie
5. **Actualización de la app** que interrumpe el servicio

### Impacto:
- ❌ **Pérdida de datos** de sesiones en curso
- ❌ **Servicio se detiene** y no registra bloqueo/desbloqueo
- ❌ **Experiencia inconsistente** para el usuario
- ❌ **Falta de confiabilidad** en la medición

---

## 🛡️ Estrategias de Robustez

### 1. Persistencia de Estado Crítico
```kotlin
// Guardar estado actual en SharedPreferences
data class AppState(
    val isServiceRunning: Boolean,
    val lastLockTimestamp: Long,
    val currentSessionStart: Long,
    val pendingSessions: List<Session>
)
```

### 2. Múltiples Estrategias de Reinicio
- **WorkManager:** Para tareas programadas de verificación
- **AlarmManager:** Para reinicios específicos
- **BroadcastReceiver:** Para eventos del sistema
- **JobScheduler:** Para Android 5.0+

### 3. Detección de Fallos
- **Health Check:** Verificación periódica del servicio
- **Auto-Recovery:** Reinicio automático si se detecta fallo
- **Logging:** Registro detallado de eventos

---

## 🏗️ Implementación Técnica

### 1. Estado Persistente
```kotlin
@Singleton
class AppStateManager @Inject constructor(
    private val context: Context
) {
    private val prefs = context.getSharedPreferences("focusly_state", Context.MODE_PRIVATE)
    
    fun saveCurrentState(state: AppState) {
        prefs.edit()
            .putBoolean("service_running", state.isServiceRunning)
            .putLong("last_lock_timestamp", state.lastLockTimestamp)
            .putLong("current_session_start", state.currentSessionStart)
            .apply()
    }
    
    fun restoreState(): AppState {
        return AppState(
            isServiceRunning = prefs.getBoolean("service_running", false),
            lastLockTimestamp = prefs.getLong("last_lock_timestamp", 0),
            currentSessionStart = prefs.getLong("current_session_start", 0),
            pendingSessions = emptyList() // Recuperar de base de datos
        )
    }
}
```

### 2. Health Check Service
```kotlin
@AndroidEntryPoint
class HealthCheckWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    @Inject
    lateinit var serviceMonitor: ServiceMonitor
    
    override suspend fun doWork(): Result {
        val isHealthy = serviceMonitor.checkServiceHealth()
        
        if (isHealthy != ServiceHealthStatus.HEALTHY) {
            // Intentar reiniciar servicio
            serviceMonitor.restartServiceIfNeeded()
            return Result.retry()
        }
        
        return Result.success()
    }
}
```

### 3. Múltiples Triggers de Reinicio
```kotlin
// 1. WorkManager - Verificación periódica
val healthCheckWork = PeriodicWorkRequestBuilder<HealthCheckWorker>(
    15, TimeUnit.MINUTES
).build()

// 2. AlarmManager - Reinicio específico
val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
val intent = Intent(context, ServiceRestartReceiver::class.java)
val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

// 3. JobScheduler - Para Android 5.0+
val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
```

### 4. BroadcastReceiver Mejorado
```kotlin
class ServiceRestartReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED -> restartService(context)
            Intent.ACTION_MY_PACKAGE_REPLACED -> restartService(context)
            "FOCUSLY_RESTART_SERVICE" -> restartService(context)
            Intent.ACTION_PACKAGE_ADDED -> checkAndRestart(context)
        }
    }
    
    private fun restartService(context: Context?) {
        context?.let {
            LockUnlockService.startService(it)
            Timber.d("Servicio reiniciado por broadcast")
        }
    }
}
```

---

## 📱 UI/UX para Robustez

### 1. Indicadores de Estado Mejorados
```kotlin
enum class ServiceStatus {
    ACTIVE,           // Verde - Todo funcionando
    RESTARTING,       // Amarillo - Reiniciando
    FAILED,           // Rojo - Error
    PERMISSION_DENIED // Gris - Sin permisos
}
```

### 2. Notificaciones Informativas
- **Servicio iniciado:** "Focusly está monitoreando tu tiempo"
- **Servicio detenido:** "Focusly se detuvo, tocá para reiniciar"
- **Reinicio automático:** "Focusly se reinició automáticamente"

### 3. Configuración de Robustez
```kotlin
data class RobustnessSettings(
    val autoRestart: Boolean = true,
    val healthCheckInterval: Int = 15, // minutos
    val maxRetryAttempts: Int = 3,
    val notificationOnRestart: Boolean = true
)
```

---

## 🧪 Testing de Robustez

### Tests de Escenarios Críticos
```kotlin
@Test
fun testAppKilledAndRestored() {
    // Simular cierre de app
    // Verificar que el servicio se reinicia
    // Verificar que no se pierden datos
}

@Test
fun testDeviceReboot() {
    // Simular reinicio del dispositivo
    // Verificar que el servicio se inicia automáticamente
}

@Test
fun testLowMemoryScenario() {
    // Simular escenario de poca memoria
    // Verificar que el servicio se mantiene
}
```

### Tests de Rendimiento
- **Consumo de batería:** <2% adicional
- **Memoria:** <20MB adicionales
- **CPU:** <1% de uso promedio

---

## 🚀 Plan de Implementación

### Sprint 1: Estado Persistente (2 días)
1. **Día 1:** Implementar AppStateManager
2. **Día 2:** Integrar con servicio existente

### Sprint 2: Health Check (2 días)
1. **Día 1:** Implementar HealthCheckWorker
2. **Día 2:** Configurar WorkManager

### Sprint 3: Múltiples Triggers (2 días)
1. **Día 1:** AlarmManager y JobScheduler
2. **Día 2:** BroadcastReceiver mejorado

### Sprint 4: UI y Testing (2 días)
1. **Día 1:** Indicadores de estado mejorados
2. **Día 2:** Testing completo de escenarios

---

## 📊 Métricas de Éxito

### Técnicas
- [ ] **Uptime:** Servicio activo 99% del tiempo
- [ ] **Recovery:** Reinicio automático en <30 segundos
- [ ] **Data Loss:** 0% de pérdida de sesiones
- [ ] **Performance:** Impacto <5% en batería

### UX
- [ ] **Transparencia:** Usuario siempre sabe el estado
- [ ] **Confianza:** App funciona sin intervención
- [ ] **Feedback:** Notificaciones claras de estado

---

## 🔧 Configuración Avanzada

### Permisos Adicionales
```xml
<!-- Para reinicio automático -->
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" />

<!-- Para WorkManager -->
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
```

### Configuración por Fabricante
```kotlin
// Detectar fabricante y aplicar configuraciones específicas
fun getManufacturerSpecificSettings(): RobustnessSettings {
    return when (Build.MANUFACTURER.lowercase()) {
        "xiaomi" -> RobustnessSettings(autoRestart = true, healthCheckInterval = 10)
        "huawei" -> RobustnessSettings(autoRestart = true, healthCheckInterval = 8)
        "samsung" -> RobustnessSettings(autoRestart = true, healthCheckInterval = 15)
        else -> RobustnessSettings()
    }
}
```

---

## 🎯 Priorización

### CRÍTICO (Implementar PRIMERO):
1. ✅ **Estado persistente** - Evitar pérdida de datos
2. ✅ **Health check** - Detectar fallos
3. ✅ **Auto-restart** - Recuperación automática

### IMPORTANTE (Después):
4. 📊 **Estadísticas** - Una vez que la app sea robusta
5. ⏰ **Cronómetro AOD** - Funcionalidad avanzada

---

¿Te parece bien este enfoque? ¿Quieres que comience con el **Sprint 1: Estado Persistente** para garantizar que no se pierdan datos cuando el usuario cierre la app? 