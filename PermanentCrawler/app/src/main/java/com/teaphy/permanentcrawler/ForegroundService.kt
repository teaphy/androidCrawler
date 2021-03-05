package com.teaphy.permanentcrawler

import android.app.*
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import com.teaphy.permanentcrawler.foreground.ForegroundUtil


/**
 * @Desc:
 * @author tiany
 * @time  2021-03-05  11:57
 * @version 1.0
 */
class ForegroundService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        throw UnsupportedOperationException("Not yet implemented");
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val pendingIntent: PendingIntent =
            Intent(this, ScrollingActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }

        val notificationChannel: NotificationChannel
//进行8.0的判断
//进行8.0的判断
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(
                ForegroundUtil.CHANNEL_DEFAULT_IMPORTANCE,
                ForegroundUtil.CHANNEL_DEFAULT_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.setShowBadge(true)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(notificationChannel)
        }

        val notification: Notification = Notification.Builder(
            this,
            ForegroundUtil.CHANNEL_DEFAULT_IMPORTANCE
        ).setContentTitle("常驻")
            .setContentText("开始常驻了.....")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setTicker("这是Tikcer")
            .build()

        startForeground(ForegroundUtil.ONGOING_NOTIFICATION_ID, notification)

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
    }
}