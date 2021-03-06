package com.teaphy.fragmentcrawler.lifecycle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.teaphy.fragmentcrawler.R
import com.teaphy.fragmentcrawler.fragment.*

class LifecycleVpActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout

    private lateinit var customPageAdapter: CustomPageAdapter

    private val listFragment = listOf(OneFragment(), TwoFragment(), ThreeFragment())
    private val listTitle = listOf("One", "Two", "Three")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lifecycle_vp_state)

        initView()
    }

    private fun initView() {
        viewPager = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tab_layout)

        customPageAdapter = CustomPageAdapter(supportFragmentManager, listFragment, listTitle)

        viewPager.adapter = customPageAdapter

        tabLayout.setupWithViewPager(viewPager)
    }
}