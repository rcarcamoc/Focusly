package com.aranthalion.focusly.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import com.aranthalion.focusly.data.entity.Session
import com.aranthalion.focusly.util.PermissionStatus
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MainScreen(
    uiState: MainUiState,
    recentSessions: List<Session>,
    onRequestNotificationPermission: () -> Unit,
    onRequestBatteryPermission: () -> Unit,
    onStartService: () -> Unit,
    onStopService: () -> Unit,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp,
                top = 80.dp // Padding superior para evitar notch y notificaciones
            )
    ) {
        // Header
        Text(
            text = "Focusly",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Monitorea tu tiempo de inactividad",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Status Card
        StatusCard(
            uiState = uiState,
            onRequestNotificationPermission = onRequestNotificationPermission,
            onRequestBatteryPermission = onRequestBatteryPermission,
            onStartService = onStartService,
            onStopService = onStopService
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Recent Sessions
        RecentSessionsCard(sessions = recentSessions)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Refresh Button
        Button(
            onClick = onRefresh,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Actualizar")
        }
    }
}

@Composable
fun StatusCard(
    uiState: MainUiState,
    onRequestNotificationPermission: () -> Unit,
    onRequestBatteryPermission: () -> Unit,
    onStartService: () -> Unit,
    onStopService: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Status Indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                StatusIndicator(status = uiState.overallStatus)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = getStatusText(uiState.overallStatus),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = getStatusDescription(uiState),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Action Buttons
            when (uiState.overallStatus) {
                OverallStatus.NEEDS_PERMISSIONS -> {
                    PermissionButtons(
                        permissionStatus = uiState.permissionStatus,
                        onRequestNotificationPermission = onRequestNotificationPermission,
                        onRequestBatteryPermission = onRequestBatteryPermission
                    )
                }
                OverallStatus.READY -> {
                    Button(
                        onClick = onStartService,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Iniciar Servicio")
                    }
                }
                OverallStatus.ACTIVE -> {
                    Button(
                        onClick = onStopService,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Detener Servicio")
                    }
                }
                OverallStatus.ERROR -> {
                    Text(
                        text = "Error en el servicio",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun StatusIndicator(status: OverallStatus) {
    val color = when (status) {
        OverallStatus.ACTIVE -> Color(0xFF4CAF50) // Verde
        OverallStatus.READY -> Color(0xFFFF9800) // Naranja
        OverallStatus.NEEDS_PERMISSIONS -> Color(0xFFFF5722) // Rojo
        OverallStatus.ERROR -> Color(0xFFF44336) // Rojo error
    }
    
    Box(
        modifier = Modifier
            .size(16.dp)
            .background(color = color, shape = CircleShape)
    )
}

@Composable
fun PermissionButtons(
    permissionStatus: PermissionStatus,
    onRequestNotificationPermission: () -> Unit,
    onRequestBatteryPermission: () -> Unit
) {
    Column {
        when (permissionStatus) {
            PermissionStatus.NONE_GRANTED -> {
                Button(
                    onClick = onRequestNotificationPermission,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Conceder Permiso de Notificaciones")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onRequestBatteryPermission,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Conceder Permiso de Batería")
                }
            }
            PermissionStatus.NOTIFICATION_MISSING -> {
                Button(
                    onClick = onRequestNotificationPermission,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Conceder Permiso de Notificaciones")
                }
            }
            PermissionStatus.BATTERY_OPTIMIZATION_MISSING -> {
                Button(
                    onClick = onRequestBatteryPermission,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Conceder Permiso de Batería")
                }
            }
            else -> {
                Text(
                    text = "Todos los permisos concedidos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun RecentSessionsCard(sessions: List<Session>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Sesiones Recientes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            if (sessions.isEmpty()) {
                Text(
                    text = "No hay sesiones registradas aún",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                LazyColumn {
                    items(sessions) { session ->
                        SessionItem(session = session)
                    }
                }
            }
        }
    }
}

@Composable
fun SessionItem(session: Session) {
    val dateFormat = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
    val startTime = Date(session.startTime)
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = dateFormat.format(startTime),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = formatDuration(session.duration),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun getStatusText(status: OverallStatus): String {
    return when (status) {
        OverallStatus.ACTIVE -> "Activo"
        OverallStatus.READY -> "Listo"
        OverallStatus.NEEDS_PERMISSIONS -> "Necesita Permisos"
        OverallStatus.ERROR -> "Error"
    }
}

private fun getStatusDescription(uiState: MainUiState): String {
    return when (uiState.overallStatus) {
        OverallStatus.ACTIVE -> "Monitoreando tiempo de inactividad"
        OverallStatus.READY -> "Servicio listo para iniciar"
        OverallStatus.NEEDS_PERMISSIONS -> "Se requieren permisos para funcionar"
        OverallStatus.ERROR -> "Error en el servicio"
    }
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