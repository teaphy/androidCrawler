package com.teaphy.coordinatorlayoutcrawler.custom

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.BarUtils
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.teaphy.coordinatorlayoutcrawler.R
import kotlin.math.absoluteValue

class CustomTitleBarActivity : AppCompatActivity() {

    private lateinit var appBarLayout: AppBarLayout
    private lateinit var titleText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_custom_title_bar)


        BarUtils.transparentStatusBar(this)
//
//        setSupportActionBar(findViewById(R.id.toolbar))
//        findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout).title = null

        appBarLayout = findViewById<AppBarLayout>(R.id.app_bar)
        titleText = findViewById(R.id.title_text)

        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { barLayout, verticalOffset ->
            when {
                verticalOffset == 0 -> {
                    titleText.visibility = View.GONE

                }
                verticalOffset.absoluteValue == barLayout.totalScrollRange -> titleText.visibility =
                    View.VISIBLE
                else -> {
                    titleText.visibility = View.GONE
                }
            }
        })

        Log.e(
            "teaphy",
            "height: ${findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout).scrimVisibleHeightTrigger}"
        )

        val tabLayout: TabLayout = findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("选项卡一"))
        tabLayout.addTab(tabLayout.newTab().setText("选项卡二"))
        tabLayout.addTab(tabLayout.newTab().setText("选项卡三"))
    }
}