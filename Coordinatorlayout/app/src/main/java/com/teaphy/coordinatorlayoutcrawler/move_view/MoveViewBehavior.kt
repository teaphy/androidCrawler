package com.teaphy.coordinatorlayoutcrawler.move_view

import android.R.attr
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.teaphy.coordinatorlayoutcrawler.R

class MoveViewBehavior : CoordinatorLayout.Behavior<View> {
    constructor() : super()

    /**
     * 需要重写构造方法，在CoordinatorLayout源码中是通过反射拿到Behavior的
     */
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    /**
     * 确定是否依赖dependency
     */
    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        return dependency.id == R.id.tv_layout_dependency
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        val offsetY: Int = dependency.top - child.top
        ViewCompat.offsetTopAndBottom(child, offsetY)
        return super.onDependentViewChanged(parent, child, dependency)
    }
}