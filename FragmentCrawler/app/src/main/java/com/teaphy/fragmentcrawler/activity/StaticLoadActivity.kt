package com.teaphy.fragmentcrawler.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.teaphy.fragmentcrawler.R

class StaticLoadActivity : AppCompatActivity() {

    private val TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        log("onCreate --> Start")

        setContentView(R.layout.activity_static_load)

        log("onCreate --> End")
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

    fun addFragment(view: View) {}
}