package com.teaphy.coordinatorlayoutcrawler.custom

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.BarUtils
import com.google.android.material.tabs.TabLayout
import com.teaphy.coordinatorlayoutcrawler.R

class CustomScrollingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE);//去标题

        setContentView(R.layout.activity_custom_scrolling)


        BarUtils.transparentStatusBar(this)

        setSupportActionBar(findViewById(R.id.toolbar))
        findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout).title = title


        val tabLayout: TabLayout = findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("选项卡一"))
        tabLayout.addTab(tabLayout.newTab().setText("选项卡二"))
        tabLayout.addTab(tabLayout.newTab().setText("选项卡三"))
    }
}