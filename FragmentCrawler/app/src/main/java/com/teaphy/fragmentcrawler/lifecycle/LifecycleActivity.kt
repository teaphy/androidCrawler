package com.teaphy.fragmentcrawler.lifecycle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import com.teaphy.fragmentcrawler.R

class LifecycleActivity : AppCompatActivity() {

    private val TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log("onCreate")
        setContentView(R.layout.activity_lifecycle)

        setLifecycle()
    }

    private fun setLifecycle() {

        val lifecycleFragment: LifecycleFragment = supportFragmentManager.findFragmentById(R.id.lifecyle_fragment) as LifecycleFragment
        with(supportFragmentManager.beginTransaction()) {
            // 此时设置Fragment的生命周期为Lifecycle.State.STARTED
            setMaxLifecycle(lifecycleFragment, Lifecycle.State.STARTED)
            commitAllowingStateLoss()
        }
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
}