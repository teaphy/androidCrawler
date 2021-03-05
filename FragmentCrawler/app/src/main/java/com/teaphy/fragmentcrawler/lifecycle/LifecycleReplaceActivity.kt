package com.teaphy.fragmentcrawler.lifecycle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.teaphy.fragmentcrawler.R
import com.teaphy.fragmentcrawler.fragment.OneFragment
import com.teaphy.fragmentcrawler.fragment.TwoFragment

class LifecycleReplaceActivity : AppCompatActivity() {

    private var oneFragment: OneFragment? = null
    private var twoFragment: TwoFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lifecycle_replace)
    }

    fun replaceOne(view: View) {
        if (oneFragment == null) {
            oneFragment = OneFragment()
        }

        with(supportFragmentManager.beginTransaction()) {
            replace(R.id.containerLayout, oneFragment!!)
//            addToBackStack("one")
            commit()
        }
    }

    fun replaceTwo(view: View) {
        if (twoFragment == null) {
            twoFragment = TwoFragment()
        }

        with(supportFragmentManager.beginTransaction()) {
            replace(R.id.containerLayout, twoFragment!!)
//            addToBackStack("two")
            commit()
        }
    }

    fun doPop(view: View) {
        supportFragmentManager.popBackStackImmediate()
    }
}