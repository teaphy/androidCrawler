package com.teaphy.fragmentcrawler.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.teaphy.fragmentcrawler.R
import com.teaphy.fragmentcrawler.fragment.OneFragment
import com.teaphy.fragmentcrawler.fragment.ThreeFragment
import com.teaphy.fragmentcrawler.fragment.TwoFragment

class DynamicLoadActivity : AppCompatActivity() {

    private val TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dynamic_load)

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

    fun addFragment(view: View) {
        val beginTransaction = supportFragmentManager.beginTransaction()

        val oneFragment = OneFragment()
        val twoFragment = TwoFragment()
        val threeFragment = ThreeFragment()

        with(beginTransaction) {
            add(R.id.content_layout, oneFragment)
            add(R.id.content_layout, twoFragment)
            add(R.id.content_layout, threeFragment)
            commit()
        }
    }

    private fun log(msg: String) {
        Log.e("teaphy", "$TAG - $msg")
    }
}