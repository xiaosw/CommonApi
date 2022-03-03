package com.xiaosw.api.floating.internal

import android.view.MotionEvent

/**
 * ClassName: [FloatWindowLayoutTouchDelegate]
 * Description:
 *
 * Create by X at 2022/03/02 17:18.
 */
internal interface FloatWindowLayoutTouchDelegate {

    fun attach(target: FloatWindowLayout<*>)

    fun handleDispatchTouchEvent(event: MotionEvent) : Boolean

}