package com.teaphy.permanentcrawler.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.teaphy.permanentcrawler.foreground.ForegroundUtil

/**
 * @Desc: 功能描述：启动时系统发出的广播的接收器
 * @author tiany
 * @time  2021-03-05  14:01
 * @version 1.0
 */
class BootBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action.equals(ForegroundUtil.ACTION_BOOT)) {
            Log.e("tepahy", "BootBroadcastReceiver - receiver: $intent?.action")
            ForegroundUtil.openForegroundService(context!!)
        }

    }

}