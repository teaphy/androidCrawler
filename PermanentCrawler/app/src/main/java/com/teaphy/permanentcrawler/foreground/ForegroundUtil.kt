package com.teaphy.permanentcrawler.foreground

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.teaphy.permanentcrawler.ForegroundService

/**
 * @Desc:
 * @author tiany
 * @time  2021-03-05  11:49
 * @version 1.0
 */
class ForegroundUtil {
    companion object {

        const val CHANNEL_DEFAULT_IMPORTANCE = "com.teaphy/channel"
        const val CHANNEL_DEFAULT_NAME = "ForegroundChannel"
        const val ONGOING_NOTIFICATION_ID = 0x1001
        var isOpenForeground: Boolean = false

        const val ACTION_BOOT = "android.intent.action.BOOT_COMPLETED"

        @RequiresApi(Build.VERSION_CODES.O)
        fun openForegroundService(context: Context) {
            if (!isOpenForeground) {

                isOpenForeground = true

                val intent = Intent(context, ForegroundService::class.java)
                context.startForegroundService(intent)
            }

        }
    }
}