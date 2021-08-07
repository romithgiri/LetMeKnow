package com.developermindset.letmeknow.utils

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class ChannelInitializer : Application() {
    val cameraChannel = "CameraChannel"
    val microphoneChannel = "MicrophoneChannel"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {

            val cameraChannel = NotificationChannel(
                cameraChannel,
                "Camera Notification Channel",
                NotificationManager.IMPORTANCE_LOW
            )

            val microphoneChannel = NotificationChannel(
                microphoneChannel,
                "Microphone Notification Channel",
                NotificationManager.IMPORTANCE_LOW
            )

            val manager1 : NotificationManager = getSystemService(NotificationManager::class.java)
            val manager2 : NotificationManager = getSystemService(NotificationManager::class.java)
            manager1.createNotificationChannel(cameraChannel)
            manager2.createNotificationChannel(microphoneChannel)
        }
    }

}