package com.aranthalion.focusly.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.aranthalion.focusly.service.LockUnlockService
import com.aranthalion.focusly.util.AppStateManager
import com.aranthalion.focusly.util.NotificationHelper
import com.aranthalion.focusly.util.ServiceHealthStatus
import com.aranthalion.focusly.util.ServiceMonitor
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class HealthCheckWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val serviceMonitor: ServiceMonitor,
    private val appStateManager: AppStateManager
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        Timber.d("HealthCheckWorker iniciado")
        
        try {
            // Verificar si el servicio está ejecutándose
            val isServiceRunning = serviceMonitor.isServiceRunning()
            val healthStatus = serviceMonitor.checkServiceHealth()
            
            // Actualizar estado en SharedPreferences
            appStateManager.updateServiceRunning(isServiceRunning)
            
            Timber.d("Estado del servicio: $healthStatus, Ejecutándose: $isServiceRunning")
            
            when (healthStatus) {
                ServiceHealthStatus.HEALTHY -> {
                    // Todo está bien, resetear intentos de reinicio
                    appStateManager.resetRestartAttempts()
                    Timber.d("Servicio saludable")
                    return Result.success()
                }
                
                ServiceHealthStatus.SERVICE_STOPPED -> {
                    // Servicio detenido, intentar reiniciar
                    Timber.w("Servicio detenido, intentando reiniciar...")
                    serviceMonitor.restartServiceIfNeeded()
                    appStateManager.incrementRestartAttempts()
                    
                    // Notificar al usuario si es necesario
                    if (appStateManager.restoreState().restartAttempts <= 3) {
                        NotificationHelper.showServiceRestartedNotification(applicationContext)
                    }
                    
                    return Result.retry()
                }
                
                ServiceHealthStatus.NO_PERMISSIONS -> {
                    // Problema de permisos, no intentar reiniciar
                    Timber.e("Problema de permisos detectado")
                    NotificationHelper.showPermissionRequiredNotification(applicationContext)
                    return Result.failure()
                }
                
                ServiceHealthStatus.ERROR -> {
                    // Error general, intentar reiniciar
                    Timber.e("Error en el servicio detectado")
                    serviceMonitor.restartServiceIfNeeded()
                    appStateManager.incrementRestartAttempts()
                    return Result.retry()
                }
            }
            
        } catch (e: Exception) {
            Timber.e(e, "Error en HealthCheckWorker")
            return Result.failure()
        }
    }
    
    companion object {
        const val WORK_NAME = "focusly_health_check"
        const val WORK_TAG = "health_check"
    }
} 