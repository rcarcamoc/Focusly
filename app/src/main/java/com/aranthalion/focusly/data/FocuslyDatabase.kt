package com.aranthalion.focusly.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.aranthalion.focusly.data.dao.SessionDao
import com.aranthalion.focusly.data.entity.Session

@Database(
    entities = [Session::class],
    version = 1,
    exportSchema = false
)
abstract class FocuslyDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    
    companion object {
        @Volatile
        private var INSTANCE: FocuslyDatabase? = null
        
        fun getDatabase(context: Context): FocuslyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FocuslyDatabase::class.java,
                    "focusly_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
} 