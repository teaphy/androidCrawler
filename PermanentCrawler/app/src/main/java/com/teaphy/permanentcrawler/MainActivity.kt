package com.teaphy.permanentcrawler

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import com.teaphy.permanentcrawler.foreground.ForegroundUtil

class MainActivity : AppCompatActivity() {

    private var isOpenForeground = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun openForegroundService(view: View?) {
        ForegroundUtil.openForegroundService(this)
    }
}