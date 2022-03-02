package com.xiaosw.api.floating.internal

import android.view.MotionEvent

/**
 * ClassName: [FloatWindowLayoutTouchDelegate]
 * Description:
 *
 * Create by X at 2022/03/02 17:18.
 */
internal interface FloatWindowLayoutTouchDelegate<T : FloatWindowLayout> {

    fun attach(target: T)

    fun handleDispatchTouchEvent(event: MotionEvent) : Boolean

}