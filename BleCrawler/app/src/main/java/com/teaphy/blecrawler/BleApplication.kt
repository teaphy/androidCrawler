package com.teaphy.blecrawler

import android.app.Application
import com.blankj.utilcode.util.Utils

/**
 *
 * Create by: teaphy
 * Date: 4/3/21
 * Time: 3:36 PM
 */
class BleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
    }
}