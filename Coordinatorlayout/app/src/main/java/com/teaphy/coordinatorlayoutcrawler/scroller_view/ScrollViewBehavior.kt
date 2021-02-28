package com.teaphy.coordinatorlayoutcrawler.scroller_view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout


/**
 * 跟随所依赖的View滚动
 * Create by: teaphy
 * Date: 2/28/21
 * Time: 7:35 PM
 */
class ScrollViewBehavior : CoordinatorLayout.Behavior<View> {

    constructor() : super()

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    /**
     * 是否要处理滑动
     */
    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return true
    }

    /**
     * 具体滑动处理
     */
    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        val offsetY = target.scrollY
        child.scrollY = offsetY
    }

}