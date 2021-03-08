package com.teaphy.coordinatorlayoutcrawler.application

import android.app.Application
import com.blankj.utilcode.util.Utils

class CustomApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
    }
}