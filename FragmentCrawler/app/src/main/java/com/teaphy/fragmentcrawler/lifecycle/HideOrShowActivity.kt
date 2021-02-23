package com.teaphy.fragmentcrawler.lifecycle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.teaphy.fragmentcrawler.R

class HideOrShowActivity : AppCompatActivity() {

    private var lifecycleFragment: LifecycleFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hide_or_show)
    }

    fun doAdd(view: View) {
        if (null == lifecycleFragment) {
            lifecycleFragment = LifecycleFragment()
            with(supportFragmentManager.beginTransaction()) {
                add(R.id.containerLayout, lifecycleFragment!!)
                commitNow()
            }
        }
    }

    fun doRemove(view: View) {
        if (null != lifecycleFragment) {
            with(supportFragmentManager.beginTransaction()) {
                remove(lifecycleFragment!!)
                commitNow()
            }
            lifecycleFragment = null
        }
    }

    fun doShow(view: View) {
        if (null != lifecycleFragment) {
            with(supportFragmentManager.beginTransaction()) {
                show(lifecycleFragment!!)
                commitNow()
            }
        }
    }

    fun doHide(view: View) {
        if (null != lifecycleFragment) {
            with(supportFragmentManager.beginTransaction()) {
                hide(lifecycleFragment!!)
                commitNow()
            }
        }
    }
}