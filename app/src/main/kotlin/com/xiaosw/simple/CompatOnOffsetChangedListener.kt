package com.xiaosw.simple

import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs

/**
 * ClassName: [CompatOnOffsetChangedListener]
 * Description:
 *
 * Create by X at 2021/05/06 16:39.
 */
abstract class CompatOnOffsetChangedListener<T : AppBarLayout> : AppBarLayout.BaseOnOffsetChangedListener<T> {
    private var mStatus: Status? = null

    final override fun onOffsetChanged(appBarLayout: T, verticalOffset: Int) {
        if (verticalOffset === 0) {
            if (mStatus != Status.EXPANDED) {
                changeStatus(appBarLayout, Status.EXPANDED, verticalOffset)
            }
        } else if (abs(verticalOffset) >= appBarLayout.totalScrollRange) {
            if (mStatus != Status.COLLAPSING) {
                changeStatus(appBarLayout, Status.COLLAPSING, verticalOffset)
            }
        } else {
            changeStatus(appBarLayout, Status.SCROLLING, verticalOffset)
        }
    }

    private inline fun changeStatus(appBarLayout: T, status: Status, verticalOffset: Int) {
        mStatus = status.also {
            onOffsetChanged(appBarLayout, verticalOffset, it)
        }
    }

    abstract fun onOffsetChanged(appBarLayout: T, verticalOffset: Int, status: Status)

    enum class Status {
        EXPANDED,
        COLLAPSING,
        SCROLLING
    }
}