package com.aranthalion.focusly.data.dao

import androidx.room.*
import com.aranthalion.focusly.data.entity.Session
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<Session>>
    
    @Query("SELECT * FROM sessions ORDER BY startTime DESC LIMIT :limit")
    fun getRecentSessions(limit: Int): Flow<List<Session>>
    
    @Insert
    suspend fun insertSession(session: Session): Long
    
    @Delete
    suspend fun deleteSession(session: Session)
    
    @Query("DELETE FROM sessions")
    suspend fun deleteAllSessions()
    
    // Consultas básicas para estadísticas
    @Query("SELECT SUM(duration) FROM sessions WHERE startTime >= :startDate")
    suspend fun getTotalDurationFromDate(startDate: Long): Long?
    
    @Query("SELECT AVG(duration) FROM sessions WHERE startTime >= :startDate")
    suspend fun getAverageDurationFromDate(startDate: Long): Long?
    
    @Query("SELECT MAX(duration) FROM sessions WHERE startTime >= :startDate")
    suspend fun getMaxDurationFromDate(startDate: Long): Long?
    
    @Query("SELECT MIN(duration) FROM sessions WHERE startTime >= :startDate")
    suspend fun getMinDurationFromDate(startDate: Long): Long?
    
    @Query("SELECT COUNT(*) FROM sessions WHERE startTime >= :startDate")
    suspend fun getSessionCountFromDate(startDate: Long): Int
} 