package com.teaphy.pagersnapdemo.snap

import android.view.View
import androidx.annotation.Nullable
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller.ScrollVectorProvider
import androidx.recyclerview.widget.SnapHelper
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * 支持star、end、center
 * 根据不同的Gravity，选择不同的RecyclerView的ItemDecoration
 */
class CustomPagerSnapHelper(var gravity: Gravity = Gravity.center) :
    SnapHelper() {

    private val INVALID_DISTANCE = 1f

    // Orientation helpers are lazily created per LayoutManager.
    @Nullable
    private var mVerticalHelper: OrientationHelper? = null

    @Nullable
    private var mHorizontalHelper: OrientationHelper? = null

    private var targetPosition: Int = 0

    // 重写此方法，对应的ItemView(即targetSnapView)当前的坐标与需要对齐的坐标之间的距离。
    //
    // 支持水平方向滚动和竖直方向滚动两个方向的计算
    override fun calculateDistanceToFinalSnap(
        layoutManager: RecyclerView.LayoutManager, targetView: View
    ): IntArray {
        val out = IntArray(2)

        // 水平方向滚动,则计算水平方向需要滚动的距离,否则水平方向的滚动距离为0
        if (layoutManager.canScrollHorizontally()) {
            // 计算对应的ItemView(即targetSnapView)当前的坐标与需要对齐的坐标之间的距离。
            out[0] = distanceToCenter(
                layoutManager, targetView,
                getHorizontalHelper(layoutManager)
            )
        } else {
            out[0] = 0
        }

        //  竖直方向滚动,则计算竖直方向需要滚动的距离,否则水平方向的滚动距离为0
        if (layoutManager.canScrollVertically()) {
            // 计算对应的ItemView(即targetSnapView)当前的坐标与需要对齐的坐标之间的距离。
            out[1] = distanceToCenter(
                layoutManager, targetView,
                getVerticalHelper(layoutManager)
            )
        } else {
            out[1] = 0
        }
        return out
    }

    // 重写此方法，根据触发Fling操作的速率（参数velocityX和参数velocityY）来找到RecyclerView需要滚
    // 动到哪个位置，该位置对应的ItemView就是那个需要进行对齐的列表项。我们把这个位置称为targetSnapPosition，
    // 对应的View称为targetSnapView。如果找不到targetSnapPosition，就返回RecyclerView.NO_POSITION
    override fun findTargetSnapPosition(
        layoutManager: RecyclerView.LayoutManager, velocityX: Int,
        velocityY: Int
    ): Int {
        // 判断layoutManager是否实现了ScrollVectorProvider
        // 如果没有实现，返回NO_POSITION
        if (layoutManager !is ScrollVectorProvider) {
            return RecyclerView.NO_POSITION
        }

        // 获取 绑定到父RecyclerView的Adapter中的Item数
        val itemCount = layoutManager.itemCount
        // 如果父RecyclerView的Adapter没有绑定Item，返回NO_POSITION
        if (itemCount == 0) {
            return RecyclerView.NO_POSITION
        }

        // 获取当前最靠近此父RecyclerView中心的ItemView,称为snapView
        val currentView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
        // 如果targetSnapView为空，表示没有需要对齐的View，返回NO_POSITION

        // 获取snapView的位置索引
        val currentPosition = layoutManager.getPosition(currentView)
        // 如果snapView的位置索引为NO_POSITION，直接返回NO_POSITION
        if (currentPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION
        }
        val vectorProvider = layoutManager as ScrollVectorProvider

        // 获取指向RecyclerView尾部方向的向量
        // computeScrollVectorForPosition用来计算指向可以找到目标位置的方向的向量。
        //
        // 速度可能与LayoutManager中子项的顺序不匹配。
        // 为了克服这个问题，要求LayoutManager提供矢量以获取方向。
        //
        // LinearSmoothScroller使用此方法返回的方向向量来启动滚动到目标位置
        val vectorForEnd = vectorProvider.computeScrollVectorForPosition(itemCount - 1)
            ?: return RecyclerView.NO_POSITION
        var vDeltaJump: Int
        var hDeltaJump: Int
        // estimateNextPositionDiffForFling用来估算SnapHelper处理fling而预计滚动的ItemView数量
        if (layoutManager.canScrollHorizontally()) {
            hDeltaJump = estimateNextPositionDiffForFling(
                layoutManager,
                getHorizontalHelper(layoutManager), velocityX, 0
            )
            if (vectorForEnd.x < 0) {
                hDeltaJump = -hDeltaJump
            }
        } else {
            hDeltaJump = 0
        }
        if (layoutManager.canScrollVertically()) {
            vDeltaJump = estimateNextPositionDiffForFling(
                layoutManager,
                getVerticalHelper(layoutManager), 0, velocityY
            )
            if (vectorForEnd.y < 0) {
                vDeltaJump = -vDeltaJump
            }
        } else {
            vDeltaJump = 0
        }
        val deltaJump = if (layoutManager.canScrollVertically()) vDeltaJump else hDeltaJump
        if (deltaJump == 0) {
            return RecyclerView.NO_POSITION
        }

        // 计算targetPosition
        var targetPos = currentPosition + deltaJump
        // 如果targetPosition小于0，则将其设置为0，即第一个Item
        if (targetPos < 0) {
            targetPos = 0
        }
        // 如果targetPosition大于或等于子项数量，则将其设置为itemCount - 1，即最后一个item
        if (targetPos >= itemCount) {
            targetPos = itemCount - 1
        }
        targetPosition = targetPos
        return targetPos
    }

    // 获取当前layoutManager上最接近对齐位置的那个view，该view称为SanpView，对应
    // 的position称为SnapPosition。如果返回null，就表示没有需要对齐的View，也就不会做滚动对齐调整。
    override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        // findCenterView用来获取当前最靠近此父RecyclerView中心的ItemView。
        if (layoutManager.canScrollVertically()) {
            return findCenterView(layoutManager, getVerticalHelper(layoutManager))
        } else if (layoutManager.canScrollHorizontally()) {
            return findCenterView(layoutManager, getHorizontalHelper(layoutManager))
        }
        return null
    }

    // 计算对应的ItemView(即targetSnapView)当前的坐标与需要对齐的坐标之间的距离。
    private fun distanceToCenter(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View, helper: OrientationHelper
    ): Int {
        // 找到targetView的相对中心坐标
        val childCenter = when (gravity) {
            // 获取ItemView开始的坐标
            Gravity.start -> helper.getDecoratedStart(targetView)
            // 获取ItemView的中心坐标
            Gravity.center -> ((helper.getDecoratedStart(targetView)
                    + helper.getDecoratedMeasurement(targetView) / 2))
            // 获取ItemView结束的的坐标
            Gravity.end -> helper.getDecoratedEnd(targetView)
        }

        // 找到容器（RecyclerView）的相对中心坐标
        val containerCenter = when (gravity) {
            // 找到容器（RecyclerView）的开始的坐标，除去paddingStart
            Gravity.start -> helper.startAfterPadding
            // 找到容器（RecyclerView）的中心坐标
            Gravity.center -> (helper.startAfterPadding + helper.totalSpace / 2)
            Gravity.end -> helper.endAfterPadding
        }
        // 两个中心坐标的差值就是targetView需要滚动的距离
//        if (gravity == Gravity.start && targetPosition == layoutManager.childCount - 1) {
//            return childCenter - containerCenter + limitOffset
//        }

        return childCenter - containerCenter
    }

    // 估算SnapHelper处理fling而预计滚动的ItemView数量
    private fun estimateNextPositionDiffForFling(
        layoutManager: RecyclerView.LayoutManager,
        helper: OrientationHelper, velocityX: Int, velocityY: Int
    ): Int {
        // 通过给定的X/Y轴上的速度来计算每个方向上的估计滚动距离。
        val distances = calculateScrollDistance(velocityX, velocityY)
        // 单个ItemView的像素数。
        val distancePerChild = computeDistancePerChild(layoutManager, helper)
        if (distancePerChild <= 0) {
            return 0
        }
        val distance =
            if (abs(distances[0]) > abs(distances[1])) distances[0] else distances[1]
        // 滚动的Item数量 = 滚动距离/单个ItemView的像素数
        return (distance / distancePerChild).roundToInt()
    }

    // 获取当前最靠近此父RecyclerView中心的ItemView。
    @Nullable
    private fun findCenterView(
        layoutManager: RecyclerView.LayoutManager,
        helper: OrientationHelper
    ): View? {
        // 获取附着到父级RecyclerView的当前ItemView数量。
        val childCount = layoutManager.childCount
        if (childCount == 0) {
            return null
        }
        var closestChild: View? = null
        // 找到容器（RecyclerView）的相对中心坐标
        val center = when (gravity) {
            // 找到容器（RecyclerView）的开始的坐标，除去paddingStart
            Gravity.start -> helper.startAfterPadding
            // 找到容器（RecyclerView）的中心坐标
            Gravity.center -> (helper.startAfterPadding + helper.totalSpace / 2)
            Gravity.end -> helper.endAfterPadding
        }
        var absClosest = Int.MAX_VALUE

        // 遍历所有的Item找到当前最靠近此父RecyclerView中心的ItemView。
        for (i in 0 until childCount) {
            val child = layoutManager.getChildAt(i)

            val childCenter = when (gravity) {
                // 获取ItemView开始的坐标
                Gravity.start -> helper.getDecoratedStart(child)
                // 获取ItemView的中心坐标
                Gravity.center -> ((helper.getDecoratedStart(child)
                        + helper.getDecoratedMeasurement(child) / 2))
                Gravity.end -> helper.getDecoratedEnd(child)
            }

            val absDistance = abs(childCenter - center)
            if (absDistance < absClosest) {
                absClosest = absDistance
                closestChild = child
            }
        }
        return closestChild
    }


    // 单个ItemView的像素数。
    private fun computeDistancePerChild(
        layoutManager: RecyclerView.LayoutManager,
        helper: OrientationHelper
    ): Float {
        var minPosView: View? = null
        var maxPosView: View? = null
        var minPos = Int.MAX_VALUE
        var maxPos = Int.MIN_VALUE
        // 获取附着到父级RecyclerView的当前ItemView数量。
        val childCount = layoutManager.childCount
        if (childCount == 0) {
            return INVALID_DISTANCE
        }
        for (i in 0 until childCount) {
            val child = layoutManager.getChildAt(i)
            val pos = layoutManager.getPosition(child!!)
            if (pos == RecyclerView.NO_POSITION) {
                continue
            }

            // 获取父RecyclerView的第一个ItemView
            if (pos < minPos) {
                minPos = pos
                minPosView = child
            }

            // 获取父RecyclerView的最后一个ItemView
            if (pos > maxPos) {
                maxPos = pos
                maxPosView = child
            }
        }
        if (minPosView == null || maxPosView == null) {
            return INVALID_DISTANCE
        }
        val start = Math.min(
            helper.getDecoratedStart(minPosView),
            helper.getDecoratedStart(maxPosView)
        )
        val end = Math.max(
            helper.getDecoratedEnd(minPosView),
            helper.getDecoratedEnd(maxPosView)
        )
        val distance = end - start
        return if (distance == 0) {
            INVALID_DISTANCE
        } else 1f * distance / (maxPos - minPos + 1)
    }

    private fun getVerticalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper {
        if (mVerticalHelper == null || mVerticalHelper!!.layoutManager !== layoutManager) {
            mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager)
        }
        return mVerticalHelper!!
    }

    private fun getHorizontalHelper(
        layoutManager: RecyclerView.LayoutManager
    ): OrientationHelper {
        if (mHorizontalHelper == null || mHorizontalHelper!!.layoutManager !== layoutManager) {
            mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager)
        }
        return mHorizontalHelper!!
    }
}
