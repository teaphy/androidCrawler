package com.teaphy.coordinatorlayoutcrawler

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.teaphy.coordinatorlayoutcrawler.custom.CustomScrollingActivity
import com.teaphy.coordinatorlayoutcrawler.google.GoogleScrollingActivity
import com.teaphy.coordinatorlayoutcrawler.move_view.MoveViewActivity
import com.teaphy.coordinatorlayoutcrawler.scroller_view.ScrollViewActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun gotoGoogleScrolling(view: View) {
        startActivity(Intent(this, GoogleScrollingActivity::class.java))
    }

    fun gotoCustomScrolling(view: View) {
        startActivity(Intent(this, CustomScrollingActivity::class.java))
    }

    fun gotoMoveView(view: View) {
        startActivity(Intent(this, MoveViewActivity::class.java))
    }

    fun gotoScrollView(view: View) {
        startActivity(Intent(this, ScrollViewActivity::class.java))
    }
}