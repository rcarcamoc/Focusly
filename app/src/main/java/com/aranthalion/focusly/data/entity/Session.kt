package com.aranthalion.focusly.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class Session(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTime: Long,
    val endTime: Long,
    val duration: Long, // en milisegundos
    val createdAt: Long = System.currentTimeMillis()
) 