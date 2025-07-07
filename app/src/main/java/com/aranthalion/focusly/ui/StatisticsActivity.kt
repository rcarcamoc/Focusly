package com.aranthalion.focusly.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aranthalion.focusly.ui.components.ChartComponent
import com.aranthalion.focusly.ui.components.ChartType
import android.graphics.Color
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticsActivity : ComponentActivity() {
    
    private val viewModel: StatisticsViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StatisticsScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun StatisticsScreen(viewModel: StatisticsViewModel) {
    val state by viewModel.statisticsState.collectAsState()
    val dailyChartData by viewModel.dailyChartData.collectAsState()
    val weeklyChartData by viewModel.weeklyChartData.collectAsState()
    val hourlyChartData by viewModel.hourlyChartData.collectAsState()
    
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
        Text(
            text = "Estadísticas de Focus",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            state.error != null -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = state.error!!,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            
            state.summary != null -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        StatisticsCard(
                            title = "Resumen General",
                            items = listOf(
                                "Total de sesiones" to "${state.summary!!.totalSessions}",
                                "Tiempo total" to viewModel.formatDuration(state.summary!!.totalDuration),
                                "Duración promedio" to viewModel.formatDuration(state.summary!!.averageDuration),
                                "Duración máxima" to viewModel.formatDuration(state.summary!!.maxDuration),
                                "Duración mínima" to viewModel.formatDuration(state.summary!!.minDuration)
                            )
                        )
                    }
                    
                    item {
                        StatisticsCard(
                            title = "Hoy",
                            items = listOf(
                                "Sesiones" to "${state.summary!!.sessionsToday}",
                                "Tiempo total" to viewModel.formatDuration(state.summary!!.durationToday)
                            )
                        )
                    }
                    
                    item {
                        StatisticsCard(
                            title = "Esta Semana",
                            items = listOf(
                                "Sesiones" to "${state.summary!!.sessionsThisWeek}",
                                "Tiempo total" to viewModel.formatDuration(state.summary!!.durationThisWeek)
                            )
                        )
                    }
                    
                    item {
                        StatisticsCard(
                            title = "Este Mes",
                            items = listOf(
                                "Sesiones" to "${state.summary!!.sessionsThisMonth}",
                                "Tiempo total" to viewModel.formatDuration(state.summary!!.durationThisMonth)
                            )
                        )
                    }
                    
                    // Gráficos
                    item {
                        ChartComponent(
                            title = "Tiempo por Día (Últimos 30 días)",
                            chartData = dailyChartData,
                            chartType = ChartType.LINE,
                            color = Color.parseColor("#4CAF50")
                        )
                    }
                    
                    item {
                        ChartComponent(
                            title = "Promedio por Día de la Semana",
                            chartData = weeklyChartData,
                            chartType = ChartType.BAR,
                            color = Color.parseColor("#2196F3")
                        )
                    }
                    
                    item {
                        ChartComponent(
                            title = "Sesiones por Hora del Día",
                            chartData = hourlyChartData,
                            chartType = ChartType.BAR,
                            color = Color.parseColor("#FF9800")
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatisticsCard(
    title: String,
    items: List<Pair<String, String>>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            items.forEach { (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = label,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = value,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
} 