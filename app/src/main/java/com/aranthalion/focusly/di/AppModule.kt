package com.aranthalion.focusly.di

import android.content.Context
import androidx.room.Room
import com.aranthalion.focusly.data.FocuslyDatabase
import com.aranthalion.focusly.data.dao.SessionDao
import com.aranthalion.focusly.data.repository.SessionRepository
import com.aranthalion.focusly.util.AppStateManager
import com.aranthalion.focusly.util.PermissionManager
import com.aranthalion.focusly.util.ServiceMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideExampleString(): String = "¡Inyección Hilt funcionando!"
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FocuslyDatabase {
        return FocuslyDatabase.getDatabase(context)
    }
    
    @Provides
    @Singleton
    fun provideSessionDao(database: FocuslyDatabase): SessionDao {
        return database.sessionDao()
    }
    
    @Provides
    @Singleton
    fun provideSessionRepository(sessionDao: SessionDao): SessionRepository {
        return SessionRepository(sessionDao)
    }
    
    @Provides
    @Singleton
    fun providePermissionManager(@ApplicationContext context: Context): PermissionManager {
        return PermissionManager(context)
    }
    
    @Provides
    @Singleton
    fun provideServiceMonitor(@ApplicationContext context: Context): ServiceMonitor {
        return ServiceMonitor(context)
    }
    
    @Provides
    @Singleton
    fun provideAppStateManager(@ApplicationContext context: Context): AppStateManager {
        return AppStateManager(context)
    }
} 