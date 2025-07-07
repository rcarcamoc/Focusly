package com.aranthalion.focusly.data

import com.aranthalion.focusly.data.entity.Session
import org.junit.Assert.assertEquals
import org.junit.Test

class SessionTest {
    
    @Test
    fun testSessionCreation() {
        val startTime = System.currentTimeMillis()
        val endTime = startTime + 300000 // 5 minutos después
        val duration = 300000L
        
        val session = Session(
            startTime = startTime,
            endTime = endTime,
            duration = duration
        )
        
        assertEquals(startTime, session.startTime)
        assertEquals(endTime, session.endTime)
        assertEquals(duration, session.duration)
        assertEquals(0L, session.id) // ID por defecto
        assert(session.createdAt > 0) // createdAt debe ser mayor que 0
    }
    
    @Test
    fun testSessionWithId() {
        val session = Session(
            id = 1L,
            startTime = 1000L,
            endTime = 4000L,
            duration = 3000L
        )
        
        assertEquals(1L, session.id)
        assertEquals(1000L, session.startTime)
        assertEquals(4000L, session.endTime)
        assertEquals(3000L, session.duration)
    }
} 