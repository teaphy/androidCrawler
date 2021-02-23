package com.teaphy.fragmentcrawler.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.teaphy.fragmentcrawler.R
import com.teaphy.fragmentcrawler.adapter.CustomPagerAdapter
import com.teaphy.fragmentcrawler.fragment.OneFragment
import com.teaphy.fragmentcrawler.fragment.ThreeFragment
import com.teaphy.fragmentcrawler.fragment.TwoFragment

class ViewPagerLoadMultiActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager

    private val TAG = javaClass.simpleName



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pager_load_multi)

        viewPager = findViewById(R.id.view_pager)
    }

    override fun onStart() {
        super.onStart()
        log("onStart")
    }

    override fun onRestart() {
        super.onRestart()
        log("onRestart")
    }

    override fun onResume() {
        super.onResume()
        log("onResume")

    }

    override fun onPause() {
        super.onPause()
        log("onPause")

    }

    override fun onStop() {
        super.onStop()
        log("onStop")

    }

    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy")

    }


    private fun log(msg: String) {
        Log.e("teaphy", "$TAG - $msg")
    }

    fun addFragment(view: View) {
        val listFragment = listOf(OneFragment(), TwoFragment(), ThreeFragment())
        val customPagerAdapter = CustomPagerAdapter(supportFragmentManager, listFragment)

        viewPager.adapter = customPagerAdapter
    }

    fun showOne(view: View) {
        viewPager.currentItem = 0
    }

    fun showTwo(view: View) {
        viewPager.currentItem = 1
    }

    fun showThree(view: View) {
        viewPager.currentItem = 2
    }
}