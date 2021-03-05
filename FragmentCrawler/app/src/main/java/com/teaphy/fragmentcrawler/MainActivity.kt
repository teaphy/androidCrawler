package com.teaphy.fragmentcrawler

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.teaphy.fragmentcrawler.activity.*
import com.teaphy.fragmentcrawler.back_stack.BackStackActivity
import com.teaphy.fragmentcrawler.communication.CommunicationActivity
import com.teaphy.fragmentcrawler.lifecycle.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun gotoLifecycle(view: View) {
        startActivity(Intent(this, LifecycleActivity::class.java))
    }

    fun gotoHideOrShow(view: View) {
        startActivity(Intent(this, HideOrShowActivity::class.java))
    }

    fun gotoLifecycleReplace(view: View) {
        startActivity(Intent(this, LifecycleReplaceActivity::class.java))
    }

    fun gotLifecycleVpState(view: View) {
        startActivity(Intent(this, LifecycleVpStateActivity::class.java))
    }

    fun gotLifecycleVp(view: View) {
        startActivity(Intent(this, LifecycleVpActivity::class.java))
    }

    fun gotoBackStack(view: View) {
        startActivity(Intent(this, BackStackActivity::class.java))
    }


    fun gotoCommunication(view: View) {
        startActivity(Intent(this, CommunicationActivity::class.java))
    }

    fun gotoStaticLoad(view: View) {
        startActivity(Intent(this, StaticLoadActivity::class.java))
    }

    fun gotoStaticLoadMulti(view: View) {
        startActivity(Intent(this, StaticLoadMultiActivity::class.java))
    }

    fun gotoDynamicLoad(view: View) {
        startActivity(Intent(this, DynamicLoadActivity::class.java))
    }

    fun gotoDynamicLoadMulti(view: View) {
        startActivity(Intent(this, DynamicLoadMultiActivity::class.java))
    }

    fun gotoLazyLoadMulti(view: View) {
        startActivity(Intent(this, DynamicLazyLoadMultiActivity::class.java))
    }

    fun gotoViewPagerLoadMulti(view: View) {
        startActivity(Intent(this, ViewPagerLoadMultiActivity::class.java))
    }


}