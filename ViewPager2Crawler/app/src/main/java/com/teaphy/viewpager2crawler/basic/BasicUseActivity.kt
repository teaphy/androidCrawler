package com.teaphy.viewpager2crawler.basic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.teaphy.viewpager2crawler.R

class BasicUseActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2

    private val listData = listOf<String>("A", "B", "C", "D", "E", "F", "G", "H", "I")
    private val mAdapter: BasicUseAdapter by lazy {
        BasicUseAdapter(listData)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic_use)

        initView()

        initListener()
    }

    private fun initView() {
        viewPager = findViewById(R.id.view_pager)

        with(viewPager) {
            adapter = mAdapter
        }
    }

    private fun initListener() {
        // 设置页面滑动的监听
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Toast.makeText(this@BasicUseActivity, "position - $position", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }


    // 设置横向
    fun setHorizontal(view: View) {
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
    }

    // 设置竖向
    fun setVertical(view: View) {
        viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
    }

    // 设置禁止滑动
    fun setInputUnenable(view: View) {
        viewPager.isUserInputEnabled = false
    }

    // 设置允许滑动
    fun setInputEnable(view: View) {
        viewPager.isUserInputEnabled = true
    }

    // 设置页间距
    fun setSpace(view: View) {
        viewPager.setPageTransformer(MarginPageTransformer(48))
    }

    // 移除页间距
    fun removeSpace(view: View) {
        viewPager.setPageTransformer(null)
    }

    // 设置CompositePageTransformer
    fun setComposeTransformer(view: View) {

        // 设置一页里面可以显示多页
        viewPager.apply {
            offscreenPageLimit = 1
            val recyclerView = getChildAt(0) as RecyclerView
            recyclerView.apply {
                val padding = 48 + 48
                // setting padding on inner RecyclerView puts overscroll effect in the right place
                setPadding(padding, 0, padding, 0)
                clipToPadding = false
            }
        }

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(ScaleInTransformer())
        compositePageTransformer.addTransformer(MarginPageTransformer(48))
        viewPager.setPageTransformer(compositePageTransformer)
    }


}