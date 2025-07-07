package com.aranthalion.focusly.data.dao

import androidx.room.*
import com.aranthalion.focusly.data.entity.Session
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions ORDER BY createdAt DESC")
    fun getAllSessions(): Flow<List<Session>>
    
    @Query("SELECT * FROM sessions ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentSessions(limit: Int): Flow<List<Session>>
    
    @Insert
    suspend fun insertSession(session: Session): Long
    
    @Delete
    suspend fun deleteSession(session: Session)
    
    @Query("DELETE FROM sessions")
    suspend fun deleteAllSessions()
} 