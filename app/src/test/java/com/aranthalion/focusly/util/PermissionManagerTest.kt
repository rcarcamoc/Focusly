package com.aranthalion.focusly.util

import android.content.Context
import android.os.Build
import android.os.PowerManager
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.TIRAMISU])
class PermissionManagerTest {
    
    @Mock
    private lateinit var mockContext: Context
    
    @Mock
    private lateinit var mockPowerManager: PowerManager
    
    private lateinit var permissionManager: PermissionManager
    
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        `when`(mockContext.packageName).thenReturn("com.aranthalion.focusly")
        `when`(mockContext.getSystemService(Context.POWER_SERVICE)).thenReturn(mockPowerManager)
        permissionManager = PermissionManager(mockContext)
    }
    
    @Test
    fun testGetPermissionStatus_AllGranted() {
        // Simular que todos los permisos están concedidos
        `when`(mockPowerManager.isIgnoringBatteryOptimizations("com.aranthalion.focusly"))
            .thenReturn(true)
        
        val status = permissionManager.getPermissionStatus()
        assertEquals(PermissionStatus.ALL_GRANTED, status)
    }
    
    @Test
    fun testGetPermissionStatus_BatteryOptimizationMissing() {
        // Simular que falta el permiso de batería
        `when`(mockPowerManager.isIgnoringBatteryOptimizations("com.aranthalion.focusly"))
            .thenReturn(false)
        
        val status = permissionManager.getPermissionStatus()
        assertEquals(PermissionStatus.BATTERY_OPTIMIZATION_MISSING, status)
    }
    
    @Test
    fun testGetPermissionStatus_NoneGranted() {
        // Simular que no hay permisos concedidos
        `when`(mockPowerManager.isIgnoringBatteryOptimizations("com.aranthalion.focusly"))
            .thenReturn(false)
        
        val status = permissionManager.getPermissionStatus()
        assertEquals(PermissionStatus.NONE_GRANTED, status)
    }
} 