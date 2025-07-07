package com.aranthalion.focusly.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.core.content.ContextCompat
import timber.log.Timber

class PermissionManager(private val context: Context) {
    
    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            true // En versiones anteriores no se requiere
        }
    }
    
    fun hasBatteryOptimizationPermission(): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val packageName = context.packageName
        return powerManager.isIgnoringBatteryOptimizations(packageName)
    }
    
    fun requestNotificationPermission(activity: android.app.Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity.requestPermissions(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_REQUEST_CODE
            )
        }
    }
    
    fun requestBatteryOptimizationPermission() {
        val intent = Intent().apply {
            action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            data = Uri.parse("package:${context.packageName}")
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
    
    fun getPermissionStatus(): PermissionStatus {
        val notificationGranted = hasNotificationPermission()
        val batteryOptimizationGranted = hasBatteryOptimizationPermission()
        
        return when {
            notificationGranted && batteryOptimizationGranted -> PermissionStatus.ALL_GRANTED
            !notificationGranted && !batteryOptimizationGranted -> PermissionStatus.NONE_GRANTED
            !notificationGranted -> PermissionStatus.NOTIFICATION_MISSING
            !batteryOptimizationGranted -> PermissionStatus.BATTERY_OPTIMIZATION_MISSING
            else -> PermissionStatus.ALL_GRANTED
        }
    }
    
    companion object {
        const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
    }
}

enum class PermissionStatus {
    ALL_GRANTED,
    NONE_GRANTED,
    NOTIFICATION_MISSING,
    BATTERY_OPTIMIZATION_MISSING
} 