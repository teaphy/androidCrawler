package com.teaphy.viewpager2crawler.fragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.teaphy.viewpager2crawler.R

class NewsActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    private val titleList = listOf("全部", "要闻", "热榜", "体育", "娱乐", "科学", "关注", "军事", "天下")

    private lateinit var listFrm: List<Fragment>


    private val customStateAdapter: CustomStateAdapter by lazy {
        CustomStateAdapter(supportFragmentManager, lifecycle, listFrm)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        initData()

        initView()
    }


    private fun initData() {
        listFrm = listOf(
            // 只有当前显示的Fragment才处于onResume状态
            NewsAllFragment.newInstance("全部"),
            NewsImportantFragment.newInstance("要闻"),
            NewsHotFragment.newInstance("热榜"),
            NewsSportsFragment.newInstance("体育"),
//            NewsFragment.newInstance("娱乐"),
//            NewsFragment.newInstance("科学"),
//            NewsFragment.newInstance("关注"),
//            NewsFragment.newInstance("军事"),
//            NewsFragment.newInstance("天下")
        )
    }

    private fun initView() {
        tabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.view_pager)

        with(viewPager) {
            offscreenPageLimit = 1
            adapter = customStateAdapter
        }

        // 可以通过TabLayoutMediator将TabLayout与ViewPager2关联
        TabLayoutMediator(tabLayout, viewPager, true) { tab, position ->
            tab.text = titleList[position]
        }.attach()
    }

}