package com.developermindset.letmeknow.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.developermindset.letmeknow.R

object Util {

    var color: Int = Color.argb(255, 228, 14, 18)

    fun showNotificationWithCustomLayout2(
        context: Context,
        notificationId: Int,
        channelId: String,
        channelName: String,
        title: String?,
        body: String?,
        intent: Intent?,
        icon: Int,
        bgColor: Int
    ) {
        val contentView = RemoteViews(context.packageName, R.layout.custom_notification)
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(
                channelId, channelName, importance
            )
            notificationManager.createNotificationChannel(mChannel)
        }

        contentView.setTextViewText(R.id.title, title)
        contentView.setTextViewText(R.id.text, body)
        contentView.setImageViewResource(R.id.image, icon)
        contentView.setImageViewResource(R.id.bg, bgColor)

        val mBuilder = NotificationCompat.Builder(context, channelId)
        mBuilder.setContent(contentView)
        mBuilder.setSmallIcon(R.drawable.ic_logo)

        val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(context)
        if (intent != null) {
            stackBuilder.addNextIntent(intent)
        }
        val resultPendingIntent: PendingIntent? = stackBuilder.getPendingIntent(
            0,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        mBuilder.setContentIntent(resultPendingIntent)
        mBuilder.setDefaults(Notification.DEFAULT_SOUND)
        mBuilder.setAutoCancel(false)
        mBuilder.setOngoing(true)
        mBuilder.setSound(alarmSound)
        mBuilder.setColorized(true)
        mBuilder.color = color
        notificationManager.notify(notificationId, mBuilder.build())
    }

    fun clearNotification(context: Context, notificationId: Int) {
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.cancel(notificationId)
    }

}