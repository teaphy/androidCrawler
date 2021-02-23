package com.teaphy.fragmentcrawler.lazy

import androidx.fragment.app.Fragment

/**
 * @Desc:
 * @author tiany
 * @time  2021-01-07  20:31
 * @version 1.0
 */
abstract class LazyFragment : Fragment() {

    // 控制是否执行懒加载
    private var isLoad = false

    // 是否 对用户可见
    private var isUserVisible = true

    /**
     * 当使用ViewPager+Fragment形式会调用该方法时，setUserVisibleHint会优先Fragment生命周期函数调用，
     * 所以这个时候就,会导致在setUserVisibleHint方法执行时就执行了懒加载，
     * 而不是在onResume方法实际调用的时候执行懒加载。所以需要这个变量
     */
    private var isCallResume = false

    override fun onResume() {
        super.onResume()

        isCallResume = true

        lazyLoad()
    }

    // show + hide
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        isUserVisible = !hidden
        lazyLoad()
    }

    // ViewPager
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        isUserVisible = isVisibleToUser
        lazyLoad()
    }

    override fun onDestroy() {
        super.onDestroy()
        isLoad = false
        isUserVisible = false
        isCallResume = false
    }

    private fun lazyLoad() {
        if (!isLoad && isCallResume && !isUserVisible) {
            loadData()
            isLoad = true
        }
    }

    abstract fun loadData()
}