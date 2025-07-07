package com.aranthalion.focusly.util

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import com.aranthalion.focusly.service.LockUnlockService
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceMonitor @Inject constructor(
    private val context: Context
) {
    
    fun isServiceRunning(): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningServices = activityManager.getRunningServices(Integer.MAX_VALUE)
        
        return runningServices.any { serviceInfo ->
            serviceInfo.service.className == LockUnlockService::class.java.name
        }
    }
    
    fun restartServiceIfNeeded() {
        if (!isServiceRunning()) {
            Timber.w("Servicio no está ejecutándose, reiniciando...")
            LockUnlockService.startService(context)
        }
    }
    
    fun checkServiceHealth(): ServiceHealthStatus {
        val isRunning = isServiceRunning()
        val hasPermissions = checkPermissions()
        
        return when {
            isRunning && hasPermissions -> ServiceHealthStatus.HEALTHY
            !isRunning && hasPermissions -> ServiceHealthStatus.SERVICE_STOPPED
            !hasPermissions -> ServiceHealthStatus.NO_PERMISSIONS
            else -> ServiceHealthStatus.ERROR
        }
    }
    
    private fun checkPermissions(): Boolean {
        val permissionManager = PermissionManager(context)
        return permissionManager.getPermissionStatus() == PermissionStatus.ALL_GRANTED
    }
    
    fun logServiceEvent(event: String) {
        Timber.i("Servicio: $event")
        // Aquí se podría implementar logging a archivo o base de datos
    }
}

enum class ServiceHealthStatus {
    HEALTHY,
    SERVICE_STOPPED,
    NO_PERMISSIONS,
    ERROR
} 