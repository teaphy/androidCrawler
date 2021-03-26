package com.teaphy.viewpager2crawler

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.teaphy.viewpager2crawler.basic.BasicUseActivity
import com.teaphy.viewpager2crawler.fragment.NewsActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun gotoBasicUse(view: View) {
        startActivity(Intent(this, BasicUseActivity::class.java))
    }

    fun gotoNews(view: View) {
        startActivity(Intent(this, NewsActivity::class.java))
    }
}