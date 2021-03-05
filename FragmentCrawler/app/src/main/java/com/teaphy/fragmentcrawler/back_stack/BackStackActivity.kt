package com.teaphy.fragmentcrawler.back_stack

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.teaphy.fragmentcrawler.R
import kotlin.math.min

class BackStackActivity : AppCompatActivity() {

    private lateinit var homText: TextView
    private lateinit var classifyText: TextView
    private lateinit var newsText: TextView
    private lateinit var mineText: TextView

    private var homeFragment: Fragment? = null
    private var classifyFragment: Fragment? = null
    private var newsFragment: Fragment? = null
    private var mineFragment: Fragment? = null

    private var mCurrentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_back_stack)

        initView()

        switchHome(null)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            // 获取当前回退栈中的Fragment个数
            var backStackCount = supportFragmentManager.backStackEntryCount

            if (backStackCount > 1) {
                // 如果回退栈中Fragment个数大于一.一直退出
                while (backStackCount > 1) {
                    with(supportFragmentManager) {
                        val tag = getBackStackEntryAt(backStackCount - 1).name
                        when (tag) {
                            "home" -> homeFragment = null
                            "classify" -> classifyFragment = null
                            "news" -> newsFragment = null
                            "mine" -> mineFragment = null
                        }

                        popBackStackImmediate()
                        backStackCount--
                    }
                }

                mCurrentFragment = homeFragment

                changeBottomUI()
            } else {
                finish()
            }
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    private fun initView() {
        homText = findViewById(R.id.homeText)
        classifyText = findViewById(R.id.classifyText)
        newsText = findViewById(R.id.newsText)
        mineText = findViewById(R.id.mineText)
    }

    fun switchHome(view: View?) {
        homeFragment?.apply {
            hideOrShow(this)
            mCurrentFragment = this
        } ?: let {
            homeFragment = HomeFragment()
            mCurrentFragment = homeFragment
            addFragment(homeFragment!!, "home")
        }

        changeBottomUI()
    }

    fun switchClassify(view: View) {
        classifyFragment?.apply {
            hideOrShow(this)
            mCurrentFragment = this
        } ?: let {
            classifyFragment = ClassifyFragment()
            mCurrentFragment = classifyFragment
            addFragment(classifyFragment!!, "classify")
        }
        changeBottomUI()
    }

    fun switchNews(view: View) {
        newsFragment?.apply {
            hideOrShow(this)
            mCurrentFragment = newsFragment
        } ?: let {
            newsFragment = NewsFragment()
            mCurrentFragment = newsFragment
            addFragment(newsFragment!!, "news")
        }
        changeBottomUI()
    }


    fun switchMine(view: View) {
        mineFragment?.apply {
            hideOrShow(this)
            mCurrentFragment = this
        } ?: let {
            mineFragment = MineFragment()
            mCurrentFragment = mineFragment
            addFragment(mineFragment!!, "mine")
        }
        changeBottomUI()
    }

    private fun addFragment(fragment: Fragment, tag: String) {
        with(supportFragmentManager.beginTransaction()) {
            add(R.id.container_layout, fragment, tag)
            addToBackStack(tag)

            hideTargetFragment(this, homeFragment, fragment)
            hideTargetFragment(this, classifyFragment, fragment)
            hideTargetFragment(this, newsFragment, fragment)
            hideTargetFragment(this, mineFragment, fragment)
            commitAllowingStateLoss()
        }


    }

    private fun hideTargetFragment(
        ft: FragmentTransaction,
        targetFragment: Fragment?,
        fragment: Fragment
    ): FragmentTransaction {

        return ft.apply {
            targetFragment?.apply {
                if (this != fragment && !isHidden) {
                    hide(this)
                }
            }
        }
    }

    private fun hideOrShow(fragment: Fragment) {
        with(supportFragmentManager.beginTransaction()) {

            doHideTargetFragment(this, homeFragment, fragment)
            doHideTargetFragment(this, classifyFragment, fragment)
            doHideTargetFragment(this, newsFragment, fragment)
            doHideTargetFragment(this, mineFragment, fragment)

            commitAllowingStateLoss()
        }
    }

    private fun doHideTargetFragment(
        ft: FragmentTransaction,
        targetFragment: Fragment?,
        fragment: Fragment
    ): FragmentTransaction {

        return ft.apply {
            targetFragment?.apply {
                if (this != fragment) {
                    hide(this)
                } else {
                    show(this)
                }
            }
        }
    }

    private fun changeBottomUI() {
        changeTextUI(homeFragment, homText)
        changeTextUI(classifyFragment, classifyText)
        changeTextUI(newsFragment, newsText)
        changeTextUI(mineFragment, mineText)
    }

    private fun changeTextUI(targetFragment: Fragment?, textView: TextView) {
        if (null != targetFragment && targetFragment == mCurrentFragment) {
            textView.setTextColor(ActivityCompat.getColor(this, android.R.color.holo_red_dark))
        } else {
            textView.setTextColor(ActivityCompat.getColor(this, android.R.color.darker_gray))
        }
    }
}