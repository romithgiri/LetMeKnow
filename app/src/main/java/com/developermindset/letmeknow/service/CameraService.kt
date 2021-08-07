package com.developermindset.letmeknow.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.developermindset.letmeknow.R
import com.developermindset.letmeknow.screen.MainActivity
import com.developermindset.letmeknow.utils.ChannelInitializer

class CameraService : Service() {
    private var mCameraManager: CameraManager? = null
    private var mCameraCallback: Any? = null
    private var notificationManager: NotificationManagerCompat? = null

    override fun onCreate() {
        super.onCreate()
        notificationManager = NotificationManagerCompat.from(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mCameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        mCameraCallback = object : CameraManager.AvailabilityCallback() {
            override fun onCameraAvailable(cameraId: String) {
                super.onCameraAvailable(cameraId)
                clearNotification(ChannelInitializer().cameraChannel, 2)
                Toast.makeText(applicationContext, "camera off", Toast.LENGTH_SHORT).show()
            }

            override fun onCameraUnavailable(cameraId: String) {
                super.onCameraUnavailable(cameraId)
                cameraNotification("${getAppName()} is using Camera")
                Toast.makeText(applicationContext, "camera on", Toast.LENGTH_SHORT).show()
            }
        }
        mCameraManager!!.registerAvailabilityCallback(
            mCameraCallback as CameraManager.AvailabilityCallback, Handler()
        )
        return START_NOT_STICKY
    }

    private fun clearNotification(channel: String, id: Int) {
        val notificationIntent = Intent(this@CameraService, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this@CameraService, 0, notificationIntent, 0)
        val notification: Notification = NotificationCompat.Builder(this@CameraService, channel)
            .setContentText("Access notifier is running in the background :)")
            .setSmallIcon(R.drawable.ic_logo)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(id, notification)
    }

    private fun cameraNotification(contentText: String) {
        val notificationIntent = Intent(this@CameraService, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this@CameraService,
            0, notificationIntent, 0
        )
        val notification: Notification =
            NotificationCompat.Builder(this@CameraService, ChannelInitializer().cameraChannel)
                .setContentTitle("Camera")
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_camera)
                .setContentIntent(pendingIntent)
                .build()
        startForeground(2, notification)
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
            pk = getAppName2(event.packageName)
        }
        return pk
    }

    private fun getAppName2(mPackageName: String): String {
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

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}