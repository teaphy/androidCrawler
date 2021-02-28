package com.teaphy.coordinatorlayoutcrawler.move_view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.teaphy.coordinatorlayoutcrawler.R


class MoveViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_move_view)

        findViewById<View>(R.id.tv_layout_dependency).setOnClickListener{
            v ->
            ViewCompat.offsetTopAndBottom(v, 30)
        }
    }
}