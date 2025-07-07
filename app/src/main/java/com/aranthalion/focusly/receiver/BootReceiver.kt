package com.aranthalion.focusly.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.aranthalion.focusly.service.LockUnlockService
import timber.log.Timber

class BootReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                Timber.d("Boot completado, iniciando servicio")
                context?.let { LockUnlockService.startService(it) }
            }
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                Timber.d("Paquete reemplazado, iniciando servicio")
                context?.let { LockUnlockService.startService(it) }
            }
        }
    }
} 