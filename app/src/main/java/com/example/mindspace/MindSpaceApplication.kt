package com.example.mindspace

import android.app.Application
import com.example.mindspace.utils.NotificationHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MindSpaceApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
    }
}
