package com.aranthalion.focusly.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aranthalion.focusly.data.entity.Session
import com.aranthalion.focusly.data.repository.SessionRepository
import com.aranthalion.focusly.service.LockUnlockService
import com.aranthalion.focusly.util.NotificationHelper
import com.aranthalion.focusly.util.PermissionManager
import com.aranthalion.focusly.util.PermissionStatus
import com.aranthalion.focusly.util.ServiceHealthStatus
import com.aranthalion.focusly.util.ServiceMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val sessionRepository: SessionRepository,
    private val serviceMonitor: ServiceMonitor
) : AndroidViewModel(application) {
    
    private val permissionManager = PermissionManager(application)
    
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    private val _recentSessions = MutableStateFlow<List<Session>>(emptyList())
    val recentSessions: StateFlow<List<Session>> = _recentSessions.asStateFlow()
    
    init {
        loadRecentSessions()
        checkPermissions()
    }
    
    fun checkPermissions() {
        val permissionStatus = permissionManager.getPermissionStatus()
        val serviceStatus = isServiceRunning()
        
        _uiState.value = _uiState.value.copy(
            permissionStatus = permissionStatus,
            serviceStatus = serviceStatus,
            overallStatus = getOverallStatus(permissionStatus, serviceStatus)
        )
        
        Timber.d("Permisos verificados: $permissionStatus, Servicio: $serviceStatus")
    }
    
    fun requestNotificationPermission() {
        // Esta función se llamará desde la Activity
        Timber.d("Solicitando permiso de notificaciones")
    }
    
    fun requestBatteryOptimizationPermission() {
        permissionManager.requestBatteryOptimizationPermission()
        Timber.d("Solicitando permiso de optimización de batería")
    }
    
    fun startService() {
        LockUnlockService.startService(getApplication())
        NotificationHelper.showServiceStartedToast(getApplication())
        checkPermissions() // Actualizar estado
        Timber.d("Servicio iniciado")
    }
    
    fun stopService() {
        LockUnlockService.stopService(getApplication())
        NotificationHelper.showServiceStoppedToast(getApplication())
        checkPermissions() // Actualizar estado
        Timber.d("Servicio detenido")
    }
    
    private fun isServiceRunning(): Boolean {
        return serviceMonitor.isServiceRunning()
    }
    
    private fun getOverallStatus(
        permissionStatus: PermissionStatus,
        serviceStatus: Boolean
    ): OverallStatus {
        return when {
            permissionStatus == PermissionStatus.ALL_GRANTED && serviceStatus -> 
                OverallStatus.ACTIVE
            permissionStatus == PermissionStatus.ALL_GRANTED && !serviceStatus -> 
                OverallStatus.READY
            permissionStatus != PermissionStatus.ALL_GRANTED -> 
                OverallStatus.NEEDS_PERMISSIONS
            else -> OverallStatus.ERROR
        }
    }
    
    private fun loadRecentSessions() {
        viewModelScope.launch {
            try {
                sessionRepository.getRecentSessions(5).collect { sessions ->
                    _recentSessions.value = sessions
                    Timber.d("Sesiones recientes cargadas: ${sessions.size}")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error cargando sesiones recientes")
            }
        }
    }
    
    fun refreshData() {
        checkPermissions()
        loadRecentSessions()
        checkServiceHealth()
    }
    
    private fun checkServiceHealth() {
        val healthStatus = serviceMonitor.checkServiceHealth()
        serviceMonitor.logServiceEvent("Estado de salud: $healthStatus")
        
        if (healthStatus == ServiceHealthStatus.SERVICE_STOPPED) {
            Timber.w("Servicio detenido, intentando reiniciar...")
            serviceMonitor.restartServiceIfNeeded()
        }
    }
}

data class MainUiState(
    val permissionStatus: PermissionStatus = PermissionStatus.NONE_GRANTED,
    val serviceStatus: Boolean = false,
    val overallStatus: OverallStatus = OverallStatus.NEEDS_PERMISSIONS,
    val isLoading: Boolean = false
)

enum class OverallStatus {
    ACTIVE,      // Verde - Todo funcionando
    READY,       // Amarillo - Permisos OK, servicio no iniciado
    NEEDS_PERMISSIONS, // Naranja - Faltan permisos
    ERROR        // Rojo - Error
} 