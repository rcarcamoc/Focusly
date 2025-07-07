package com.aranthalion.focusly

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.aranthalion.focusly.ui.MainScreen
import com.aranthalion.focusly.ui.MainUiState
import com.aranthalion.focusly.ui.MainViewModel
import com.aranthalion.focusly.ui.StatisticsActivity
import com.aranthalion.focusly.ui.theme.FocuslyTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private val viewModel: MainViewModel by viewModels()
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Timber.d("Permiso de notificaciones concedido")
            viewModel.checkPermissions()
        } else {
            Timber.d("Permiso de notificaciones denegado")
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            FocuslyTheme {
                val uiState by viewModel.uiState.collectAsState()
                val recentSessions by viewModel.recentSessions.collectAsState()
                
                MainScreen(
                    uiState = uiState,
                    recentSessions = recentSessions,
                    onRequestNotificationPermission = {
                        requestNotificationPermission()
                    },
                    onRequestBatteryPermission = {
                        viewModel.requestBatteryOptimizationPermission()
                    },
                    onStartService = {
                        viewModel.startService()
                    },
                    onStopService = {
                        viewModel.stopService()
                    },
                    onRefresh = {
                        viewModel.refreshData()
                    },
                    onOpenStatistics = {
                        startActivity(Intent(this@MainActivity, StatisticsActivity::class.java))
                    }
                )
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Verificar permisos cuando la actividad se reanuda
        viewModel.checkPermissions()
    }
    
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Timber.d("Permiso de notificaciones ya concedido")
                    viewModel.checkPermissions()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Mostrar explicación al usuario
                    Timber.d("Mostrar explicación de permiso de notificaciones")
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    // Solicitar permiso directamente
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
}