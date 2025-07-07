package com.aranthalion.focusly.data.model

import androidx.room.ColumnInfo

data class DayStats(
    @ColumnInfo(name = "dayOfWeek") val dayOfWeek: String,
    @ColumnInfo(name = "sessionCount") val sessionCount: Int,
    @ColumnInfo(name = "avgDuration") val avgDuration: Long,
    @ColumnInfo(name = "totalDuration") val totalDuration: Long
)

data class HourStats(
    @ColumnInfo(name = "hourOfDay") val hourOfDay: String,
    @ColumnInfo(name = "sessionCount") val sessionCount: Int,
    @ColumnInfo(name = "avgDuration") val avgDuration: Long,
    @ColumnInfo(name = "totalDuration") val totalDuration: Long
)

data class DailyStats(
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "sessionCount") val sessionCount: Int,
    @ColumnInfo(name = "totalDuration") val totalDuration: Long
)

data class StatisticsSummary(
    val totalSessions: Int,
    val totalDuration: Long,
    val averageDuration: Long,
    val maxDuration: Long,
    val minDuration: Long,
    val sessionsToday: Int,
    val durationToday: Long,
    val sessionsThisWeek: Int,
    val durationThisWeek: Long,
    val sessionsThisMonth: Int,
    val durationThisMonth: Long
)

data class ChartData(
    val labels: List<String>,
    val values: List<Float>,
    val colors: List<Int> = emptyList()
) 