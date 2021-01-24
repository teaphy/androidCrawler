package com.teaphy.pagersnapdemo.center

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

// 当Gravity为Gravity.Start时，RecyclerView的Item之间的间距设置
class CenterSpacesItemDecoration(
    private val space: Int,
    private val isVertical: Boolean = false,
    private val limitOffSet: Int = 0
) :
    ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {

        when {
            // 第一个Item
            parent.getChildLayoutPosition(view) == 0 -> {
                with(outRect) {
                    left = space + limitOffSet / 2
                    right = 0
                    bottom = if (isVertical) {
                        space
                    } else {
                        0
                    }

                    top = if (isVertical) {
                        space
                    } else {
                        0
                    }
                }
            }
            // 最后一个Item
            parent.getChildLayoutPosition(view) == state.itemCount - 1 -> {
                with(outRect) {
                    left = space
                    right = limitOffSet / 2
                    bottom = if (isVertical) {
                        space
                    } else {
                        0
                    }

                    top = if (isVertical) {
                        space
                    } else {
                        0
                    }
                }
            }
            else -> {
                with(outRect) {
                    left = space
                    right = 0
                    bottom = if (isVertical) {
                        space
                    } else {
                        0
                    }

                    top = if (isVertical) {
                        space
                    } else {
                        0
                    }
                }
            }
        }
    }
}