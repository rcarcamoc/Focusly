package com.aranthalion.focusly

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class Focusly : Application() {
    override fun onCreate() {
        super.onCreate()
        // Configurar Timber para logging en debug
        Timber.plant(Timber.DebugTree())
    }
} 