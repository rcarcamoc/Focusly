package com.aranthalion.focusly.data.repository

import com.aranthalion.focusly.data.dao.SessionDao
import com.aranthalion.focusly.data.model.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatisticsRepository @Inject constructor(
    private val sessionDao: SessionDao
) {
    
    suspend fun getStatisticsSummary(): StatisticsSummary {
        val now = System.currentTimeMillis()
        val startOfDay = getStartOfDay(now)
        val startOfWeek = getStartOfWeek(now)
        val startOfMonth = getStartOfMonth(now)
        
        return try {
            val totalSessions = sessionDao.getSessionCountFromDate(0)
            val totalDuration = sessionDao.getTotalDurationFromDate(0) ?: 0L
            val averageDuration = sessionDao.getAverageDurationFromDate(0) ?: 0L
            val maxDuration = sessionDao.getMaxDurationFromDate(0) ?: 0L
            val minDuration = sessionDao.getMinDurationFromDate(0) ?: 0L
            
            val sessionsToday = sessionDao.getSessionCountFromDate(startOfDay)
            val durationToday = sessionDao.getTotalDurationFromDate(startOfDay) ?: 0L
            
            val sessionsThisWeek = sessionDao.getSessionCountFromDate(startOfWeek)
            val durationThisWeek = sessionDao.getTotalDurationFromDate(startOfWeek) ?: 0L
            
            val sessionsThisMonth = sessionDao.getSessionCountFromDate(startOfMonth)
            val durationThisMonth = sessionDao.getTotalDurationFromDate(startOfMonth) ?: 0L
            
            StatisticsSummary(
                totalSessions = totalSessions,
                totalDuration = totalDuration,
                averageDuration = averageDuration,
                maxDuration = maxDuration,
                minDuration = minDuration,
                sessionsToday = sessionsToday,
                durationToday = durationToday,
                sessionsThisWeek = sessionsThisWeek,
                durationThisWeek = durationThisWeek,
                sessionsThisMonth = sessionsThisMonth,
                durationThisMonth = durationThisMonth
            )
        } catch (e: Exception) {
            Timber.e(e, "Error obteniendo estadísticas")
            StatisticsSummary(
                totalSessions = 0,
                totalDuration = 0L,
                averageDuration = 0L,
                maxDuration = 0L,
                minDuration = 0L,
                sessionsToday = 0,
                durationToday = 0L,
                sessionsThisWeek = 0,
                durationThisWeek = 0L,
                sessionsThisMonth = 0,
                durationThisMonth = 0L
            )
        }
    }
    
    // Métodos simplificados para estadísticas básicas
    suspend fun getTotalDuration(days: Int = 30): Long {
        val startDate = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000L)
        return try {
            sessionDao.getTotalDurationFromDate(startDate) ?: 0L
        } catch (e: Exception) {
            Timber.e(e, "Error obteniendo duración total")
            0L
        }
    }
    
    suspend fun getAverageDuration(days: Int = 30): Long {
        val startDate = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000L)
        return try {
            sessionDao.getAverageDurationFromDate(startDate) ?: 0L
        } catch (e: Exception) {
            Timber.e(e, "Error obteniendo duración promedio")
            0L
        }
    }
    
    suspend fun getSessionCount(days: Int = 30): Int {
        val startDate = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000L)
        return try {
            sessionDao.getSessionCountFromDate(startDate)
        } catch (e: Exception) {
            Timber.e(e, "Error obteniendo conteo de sesiones")
            0
        }
    }
    
    private fun getStartOfDay(timestamp: Long): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    private fun getStartOfWeek(timestamp: Long): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(java.util.Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    private fun getStartOfMonth(timestamp: Long): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
} 