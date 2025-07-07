package com.aranthalion.focusly.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.aranthalion.focusly.R
import timber.log.Timber

object NotificationHelper {
    
    private const val CHANNEL_ID = "focusly_notifications"
    private const val CHANNEL_NAME = "Focusly Notifications"
    
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones de Focusly"
                setShowBadge(false)
            }
            
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
            Timber.d("Canal de notificaciones creado")
        }
    }
    
    fun showInactivityToast(context: Context, durationMs: Long) {
        val formattedTime = formatDuration(durationMs)
        val message = "Tiempo inactivo: $formattedTime"
        
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        Timber.d("Toast mostrado: $message")
    }
    
    fun showServiceStartedToast(context: Context) {
        Toast.makeText(context, "Servicio iniciado", Toast.LENGTH_SHORT).show()
    }
    
    fun showServiceStoppedToast(context: Context) {
        Toast.makeText(context, "Servicio detenido", Toast.LENGTH_SHORT).show()
    }
    
    fun showPermissionRequiredToast(context: Context, permissionType: String) {
        Toast.makeText(
            context,
            "Se requiere permiso de $permissionType",
            Toast.LENGTH_LONG
        ).show()
    }
    
    fun showServiceRestartedNotification(context: Context) {
        Toast.makeText(
            context,
            "Focusly se reinició automáticamente",
            Toast.LENGTH_SHORT
        ).show()
    }
    
    fun showPermissionRequiredNotification(context: Context) {
        Toast.makeText(
            context,
            "Focusly necesita permisos para funcionar correctamente",
            Toast.LENGTH_LONG
        ).show()
    }
    
    private fun formatDuration(durationMs: Long): String {
        val seconds = durationMs / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        
        return when {
            hours > 0 -> "${hours}h ${minutes % 60}m"
            minutes > 0 -> "${minutes}m ${seconds % 60}s"
            else -> "${seconds}s"
        }
    }
} 