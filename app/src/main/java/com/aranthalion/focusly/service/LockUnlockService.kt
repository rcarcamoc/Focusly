package com.aranthalion.focusly.service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.aranthalion.focusly.R
import com.aranthalion.focusly.data.entity.Session
import com.aranthalion.focusly.data.repository.SessionRepository
import com.aranthalion.focusly.util.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LockUnlockService : Service() {
    
    @Inject
    lateinit var sessionRepository: SessionRepository
    
    private var lockTimestamp: Long = 0
    private lateinit var lockUnlockReceiver: BroadcastReceiver
    
    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "focusly_service_channel"
        private const val CHANNEL_NAME = "Focusly Service"
        
        fun startService(context: Context) {
            val intent = Intent(context, LockUnlockService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
        
        fun stopService(context: Context) {
            val intent = Intent(context, LockUnlockService::class.java)
            context.stopService(intent)
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        Timber.d("LockUnlockService onCreate")
        NotificationHelper.createNotificationChannel(this)
        setupNotificationChannel()
        setupBroadcastReceiver()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("LockUnlockService onStartCommand")
        startForeground(NOTIFICATION_ID, createNotification())
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        super.onDestroy()
        Timber.d("LockUnlockService onDestroy")
        try {
            unregisterReceiver(lockUnlockReceiver)
        } catch (e: Exception) {
            Timber.e(e, "Error unregistering receiver")
        }
    }
    
    private fun setupNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Canal para el servicio de Focusly"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Focusly")
            .setContentText("Monitoreando tiempo de inactividad")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setAutoCancel(false)
            .build()
    }
    
    private fun setupBroadcastReceiver() {
        lockUnlockReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Intent.ACTION_SCREEN_OFF -> {
                        Timber.d("Pantalla bloqueada")
                        lockTimestamp = System.currentTimeMillis()
                    }
                    Intent.ACTION_USER_PRESENT -> {
                        Timber.d("Usuario presente")
                        handleUnlock()
                    }
                }
            }
        }
        
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_USER_PRESENT)
        }
        
        registerReceiver(lockUnlockReceiver, filter)
    }
    
    private fun handleUnlock() {
        if (lockTimestamp > 0) {
            val currentTime = System.currentTimeMillis()
            val duration = currentTime - lockTimestamp
            
            // Mostrar Toast con el tiempo de inactividad
            NotificationHelper.showInactivityToast(this, duration)
            
            // Guardar sesión en la base de datos
            saveSession(lockTimestamp, currentTime, duration)
            
            // Resetear timestamp
            lockTimestamp = 0
            
            Timber.d("Sesión guardada: ${sessionRepository.formatDuration(duration)}")
        }
    }
    
    private fun saveSession(startTime: Long, endTime: Long, duration: Long) {
        val session = Session(
            startTime = startTime,
            endTime = endTime,
            duration = duration
        )
        
        // Usar coroutine para guardar en la base de datos
        CoroutineScope(Dispatchers.IO).launch {
            try {
                sessionRepository.insertSession(session)
                Timber.d("Sesión guardada exitosamente")
            } catch (e: Exception) {
                Timber.e(e, "Error guardando sesión")
            }
        }
    }
    

} 