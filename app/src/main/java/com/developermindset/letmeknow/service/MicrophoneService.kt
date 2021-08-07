package com.developermindset.letmeknow.service

import android.app.ActivityManager
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.AudioRecordingConfiguration
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.developermindset.letmeknow.R
import com.developermindset.letmeknow.screen.MainActivity
import com.developermindset.letmeknow.utils.ChannelInitializer

class MicrophoneService : Service() {
    private val TAG = "MicrophoneService"
    private var mAudioManager: AudioManager? = null
    private var mAudioCallback: Any? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            mAudioCallback = object : AudioManager.AudioRecordingCallback() {
                override fun onRecordingConfigChanged(configs: List<AudioRecordingConfiguration>) {
                    super.onRecordingConfigChanged(configs)
                    if (configs.isNotEmpty()) {
                        microphoneNotification("${getAppName()} is using Microphone")
                    } else {
                        clearNotification(ChannelInitializer().microphoneChannel, 1)
                    }
                }
            }
            mAudioManager!!.registerAudioRecordingCallback(
                mAudioCallback as AudioManager.AudioRecordingCallback, Handler()
            )
        }

        return START_NOT_STICKY
    }

    private fun clearNotification(channel: String, id: Int) {
        stopForeground(true)
    }

    private fun microphoneNotification(contentText: String) {
        val notificationIntent = Intent(this@MicrophoneService, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this@MicrophoneService,
            0, notificationIntent, 0
        )
        val notification: Notification =
            NotificationCompat.Builder(
                this@MicrophoneService,
                ChannelInitializer().microphoneChannel
            ).setContentTitle("Microphone In Use")
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_mic)
                .setContentIntent(pendingIntent)
                .build()
        startForeground(1, notification)
    }

    private fun getAppName(): String {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        manager.getRunningTasks(5)
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
        Log.i(TAG, "OnBind")
        return null
    }
}