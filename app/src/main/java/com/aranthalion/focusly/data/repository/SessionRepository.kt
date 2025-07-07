package com.aranthalion.focusly.data.repository

import com.aranthalion.focusly.data.dao.SessionDao
import com.aranthalion.focusly.data.entity.Session
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepository @Inject constructor(
    private val sessionDao: SessionDao
) {
    
    fun getAllSessions(): Flow<List<Session>> {
        return sessionDao.getAllSessions()
    }
    
    fun getRecentSessions(limit: Int): Flow<List<Session>> {
        return sessionDao.getRecentSessions(limit)
    }
    
    suspend fun insertSession(session: Session): Long {
        return try {
            sessionDao.insertSession(session)
        } catch (e: Exception) {
            Timber.e(e, "Error insertando sesión")
            -1
        }
    }
    
    suspend fun deleteSession(session: Session) {
        try {
            sessionDao.deleteSession(session)
        } catch (e: Exception) {
            Timber.e(e, "Error eliminando sesión")
        }
    }
    
    suspend fun deleteAllSessions() {
        try {
            sessionDao.deleteAllSessions()
        } catch (e: Exception) {
            Timber.e(e, "Error eliminando todas las sesiones")
        }
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
} 