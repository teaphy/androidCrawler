package com.teaphy.fragmentcrawler.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.teaphy.fragmentcrawler.R
import com.teaphy.fragmentcrawler.lazy.OneLazyFragment
import com.teaphy.fragmentcrawler.lazy.ThreeLazyFragment
import com.teaphy.fragmentcrawler.lazy.TwoLazyFragment

class DynamicLazyLoadMultiActivity : AppCompatActivity() {

    private val TAG = javaClass.simpleName

    val oneFragment = OneLazyFragment()
    val twoFragment = TwoLazyFragment()
    val threeFragment = ThreeLazyFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dynamic_load_multi)
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
        val beginTransaction = supportFragmentManager.beginTransaction()

        with(beginTransaction) {
            add(R.id.content_layout, oneFragment)
            add(R.id.content_layout, twoFragment)
            add(R.id.content_layout, threeFragment)

            show(oneFragment)
            hide(twoFragment)
            hide(threeFragment)

            commit()
        }
    }

    fun showOne(view: View) {
        val beginTransaction = supportFragmentManager.beginTransaction()

        with(beginTransaction) {
            show(oneFragment)
            hide(twoFragment)
            hide(threeFragment)
            commit()
        }
    }

    fun showTwo(view: View) {
        val beginTransaction = supportFragmentManager.beginTransaction()

        with(beginTransaction) {
            hide(oneFragment)
            show(twoFragment)
            hide(threeFragment)
            commit()
        }
    }

    fun showThree(view: View) {
        val beginTransaction = supportFragmentManager.beginTransaction()

        with(beginTransaction) {
            hide(oneFragment)
            hide(twoFragment)
            show(threeFragment)
            commit()
        }
    }
}