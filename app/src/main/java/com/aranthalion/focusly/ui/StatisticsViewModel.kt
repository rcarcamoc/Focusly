package com.aranthalion.focusly.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aranthalion.focusly.data.model.StatisticsSummary
import com.aranthalion.focusly.data.repository.StatisticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val statisticsRepository: StatisticsRepository
) : ViewModel() {
    
    private val _statisticsState = MutableStateFlow(StatisticsState())
    val statisticsState: StateFlow<StatisticsState> = _statisticsState.asStateFlow()
    
    init {
        loadStatistics()
    }
    
    fun loadStatistics() {
        viewModelScope.launch {
            try {
                _statisticsState.value = _statisticsState.value.copy(isLoading = true)
                
                val summary = statisticsRepository.getStatisticsSummary()
                
                _statisticsState.value = StatisticsState(
                    isLoading = false,
                    summary = summary,
                    error = null
                )
                
                Timber.d("Estadísticas cargadas: $summary")
                
            } catch (e: Exception) {
                Timber.e(e, "Error cargando estadísticas")
                _statisticsState.value = StatisticsState(
                    isLoading = false,
                    error = "Error cargando estadísticas: ${e.message}"
                )
            }
        }
    }
    
    fun refreshStatistics() {
        loadStatistics()
    }
    
    fun formatDuration(durationMs: Long): String {
        val seconds = durationMs / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        
        return when {
            hours > 0 -> "${hours}h ${minutes % 60}m"
            minutes > 0 -> "${minutes}m ${seconds % 60}s"
            else -> "${seconds}s"
        }
    }
    
    fun formatDurationMinutes(durationMs: Long): String {
        val minutes = durationMs / (1000 * 60)
        return "${minutes}m"
    }
}

data class StatisticsState(
    val isLoading: Boolean = false,
    val summary: StatisticsSummary? = null,
    val error: String? = null
) 