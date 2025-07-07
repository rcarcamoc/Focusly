package com.aranthalion.focusly.util

import android.content.Context
import android.content.SharedPreferences
import com.aranthalion.focusly.data.entity.Session
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppStateManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "focusly_state", 
        Context.MODE_PRIVATE
    )
    
    companion object {
        private const val KEY_SERVICE_RUNNING = "service_running"
        private const val KEY_LAST_LOCK_TIMESTAMP = "last_lock_timestamp"
        private const val KEY_CURRENT_SESSION_START = "current_session_start"
        private const val KEY_PENDING_SESSIONS_COUNT = "pending_sessions_count"
        private const val KEY_LAST_HEALTH_CHECK = "last_health_check"
        private const val KEY_RESTART_ATTEMPTS = "restart_attempts"
        private const val KEY_LAST_KNOWN_DEVICE_STATE = "last_known_device_state"
    }
    
    fun saveCurrentState(state: AppState) {
        prefs.edit()
            .putBoolean(KEY_SERVICE_RUNNING, state.isServiceRunning)
            .putLong(KEY_LAST_LOCK_TIMESTAMP, state.lastLockTimestamp)
            .putLong(KEY_CURRENT_SESSION_START, state.currentSessionStart)
            .putInt(KEY_PENDING_SESSIONS_COUNT, state.pendingSessions.size)
            .putLong(KEY_LAST_HEALTH_CHECK, System.currentTimeMillis())
            .putInt(KEY_RESTART_ATTEMPTS, state.restartAttempts)
            .putString(KEY_LAST_KNOWN_DEVICE_STATE, state.lastKnownDeviceState.name)
            .apply()
        
        Timber.d("Estado guardado: $state")
    }
    
    fun restoreState(): AppState {
        val state = AppState(
            isServiceRunning = prefs.getBoolean(KEY_SERVICE_RUNNING, false),
            lastLockTimestamp = prefs.getLong(KEY_LAST_LOCK_TIMESTAMP, 0),
            currentSessionStart = prefs.getLong(KEY_CURRENT_SESSION_START, 0),
            pendingSessions = emptyList(), // Se recuperará de la base de datos
            restartAttempts = prefs.getInt(KEY_RESTART_ATTEMPTS, 0),
            lastKnownDeviceState = DeviceState.valueOf(
                prefs.getString(KEY_LAST_KNOWN_DEVICE_STATE, DeviceState.UNKNOWN.name) ?: DeviceState.UNKNOWN.name
            )
        )
        
        Timber.d("Estado restaurado: $state")
        return state
    }
    
    fun updateServiceRunning(isRunning: Boolean) {
        prefs.edit()
            .putBoolean(KEY_SERVICE_RUNNING, isRunning)
            .putLong(KEY_LAST_HEALTH_CHECK, System.currentTimeMillis())
            .apply()
        
        Timber.d("Servicio actualizado: $isRunning")
    }
    
    fun updateLockTimestamp(timestamp: Long) {
        prefs.edit()
            .putLong(KEY_LAST_LOCK_TIMESTAMP, timestamp)
            .apply()
        
        Timber.d("Timestamp de bloqueo actualizado: $timestamp")
    }
    
    fun updateCurrentSessionStart(timestamp: Long) {
        prefs.edit()
            .putLong(KEY_CURRENT_SESSION_START, timestamp)
            .apply()
        
        Timber.d("Inicio de sesión actualizado: $timestamp")
    }
    
    fun incrementRestartAttempts() {
        val currentAttempts = prefs.getInt(KEY_RESTART_ATTEMPTS, 0)
        prefs.edit()
            .putInt(KEY_RESTART_ATTEMPTS, currentAttempts + 1)
            .apply()
        
        Timber.d("Intentos de reinicio incrementados: ${currentAttempts + 1}")
    }
    
    fun resetRestartAttempts() {
        prefs.edit()
            .putInt(KEY_RESTART_ATTEMPTS, 0)
            .apply()
        
        Timber.d("Intentos de reinicio reseteados")
    }
    
    fun getLastHealthCheck(): Long {
        return prefs.getLong(KEY_LAST_HEALTH_CHECK, 0)
    }
    
    fun shouldPerformHealthCheck(): Boolean {
        val lastCheck = getLastHealthCheck()
        val currentTime = System.currentTimeMillis()
        val timeSinceLastCheck = currentTime - lastCheck
        val healthCheckInterval = 15 * 60 * 1000L // 15 minutos
        
        return timeSinceLastCheck >= healthCheckInterval
    }
    
    fun clearState() {
        prefs.edit().clear().apply()
        Timber.d("Estado limpiado")
    }
}

data class AppState(
    val isServiceRunning: Boolean = false,
    val lastLockTimestamp: Long = 0,
    val currentSessionStart: Long = 0,
    val pendingSessions: List<Session> = emptyList(),
    val restartAttempts: Int = 0,
    val lastKnownDeviceState: DeviceState = DeviceState.UNKNOWN
)

enum class DeviceState {
    LOCKED,
    UNLOCKED,
    UNKNOWN
} 