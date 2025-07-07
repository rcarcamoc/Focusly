package com.aranthalion.focusly

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.aranthalion.focusly.work.HealthCheckWorker
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class Focusly : Application() {
    override fun onCreate() {
        super.onCreate()
        // Configurar Timber para logging en debug
        Timber.plant(Timber.DebugTree())
        
        // Configurar WorkManager para health checks
        setupWorkManager()
    }
    
    private fun setupWorkManager() {
        val workManager = WorkManager.getInstance(applicationContext)
        
        // Programar health check periódico (cada 15 minutos)
        val healthCheckRequest = androidx.work.PeriodicWorkRequestBuilder<HealthCheckWorker>(
            java.time.Duration.ofMinutes(15)
        ).addTag(HealthCheckWorker.WORK_TAG)
            .build()
        
        workManager.enqueueUniquePeriodicWork(
            HealthCheckWorker.WORK_NAME,
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            healthCheckRequest
        )
        
        Timber.d("WorkManager configurado para health checks")
    }
} 