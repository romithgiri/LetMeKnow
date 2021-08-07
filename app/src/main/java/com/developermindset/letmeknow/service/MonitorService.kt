package com.developermindset.letmeknow.service

import android.app.*
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.media.AudioRecordingConfiguration
import android.os.Build
import android.os.Handler
import android.os.IBinder
import com.developermindset.letmeknow.R
import com.developermindset.letmeknow.screen.MainActivity
import com.developermindset.letmeknow.utils.StoreData
import com.developermindset.letmeknow.utils.Util

class MonitorService : Service() {
    private var mCameraManager: CameraManager? = null
    private var mCameraCallback: Any? = null

    private var mAudioManager: AudioManager? = null
    private var mAudioCallback: Any? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val notification = createNotification()
        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //for camera
        mCameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        mCameraCallback = object : CameraManager.AvailabilityCallback() {
            override fun onCameraAvailable(cameraId: String) {
                super.onCameraAvailable(cameraId)
                if (StoreData(this@MonitorService).getIsCameraServiceEnable()) {
                    Util.clearNotification(this@MonitorService, 101)
                }
            }

            override fun onCameraUnavailable(cameraId: String) {
                super.onCameraUnavailable(cameraId)
                if (StoreData(this@MonitorService).getIsCameraServiceEnable()) {
                    val intent = Intent(this@MonitorService, MainActivity::class.java)
                    intent.putExtra("isStartFromDeepLink", true)
                    Util.showNotificationWithCustomLayout2(
                        this@MonitorService,
                        101,
                        "Camera",
                        "Camera",
                        "Camera alert",
                        "${getAppName()} is using camera",
                        intent,
                        R.drawable.ic_camera,
                        R.color.app_background
                    )
                }
            }
        }

        mCameraManager!!.registerAvailabilityCallback(
            mCameraCallback as CameraManager.AvailabilityCallback, Handler()
        )

        //for microphone
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            mAudioCallback = object : AudioManager.AudioRecordingCallback() {
                override fun onRecordingConfigChanged(configs: List<AudioRecordingConfiguration>) {
                    super.onRecordingConfigChanged(configs)
                    if (StoreData(this@MonitorService).getIsMicroPhoneServiceEnable()) {
                        if (configs.isNotEmpty()) {
                            Util.showNotificationWithCustomLayout2(
                                this@MonitorService,
                                102,
                                "Microphone",
                                "Microphone",
                                "Microphone Usage alert",
                                "${getAppName()} is using microphone",
                                intent,
                                R.drawable.ic_mic,
                                R.color.app_background
                            )
                        } else {
                            Util.clearNotification(this@MonitorService, 102)
                        }
                    }
                }
            }
            mAudioManager!!.registerAudioRecordingCallback(
                mAudioCallback as AudioManager.AudioRecordingCallback, Handler()
            )
        }

        return START_NOT_STICKY
    }

    private fun getAppName(): String {
        var pk = ""
        val usage =
            applicationContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()
        val beginTime = endTime - 5000
        val event = UsageEvents.Event()
        val usageEvents = usage.queryEvents(beginTime, endTime)
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            pk = getAppLabel(event.packageName)
        }
        return pk
    }

    private fun getAppLabel(mPackageName: String): String {
        val packageManager = applicationContext.packageManager
        return try {
            packageManager.getApplicationLabel(
                packageManager.getApplicationInfo(
                    mPackageName,
                    PackageManager.GET_META_DATA
                )
            ) as String
        } catch (exp: PackageManager.NameNotFoundException) {
            mPackageName
        }
    }

    private fun createNotification(): Notification {
        val notificationChannelId = resources.getString(R.string.notification_channel)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                notificationChannelId,
                resources.getString(R.string.notification_title),
                NotificationManager.IMPORTANCE_HIGH
            ).let {
                it.description = resources.getString(R.string.notification_description)
                it.enableLights(true)
                it.lightColor = Color.RED
                it.enableVibration(true)
                it.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                it
            }
            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }

        val builder: Notification.Builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(
                this,
                notificationChannelId
            ) else Notification.Builder(this)

        return builder
            .setContentTitle(resources.getString(R.string.notification_title))
            .setContentText(resources.getString(R.string.notification_description))
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_logo)
            .setTicker(resources.getString(R.string.notification_title))
            .setPriority(Notification.PRIORITY_HIGH) // for under android 26 compatibility
            .build()
    }

}