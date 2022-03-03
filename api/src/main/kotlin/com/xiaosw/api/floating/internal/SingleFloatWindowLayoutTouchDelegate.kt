package com.xiaosw.api.floating.internal

import android.animation.ObjectAnimator
import android.content.Context
import android.view.MotionEvent
import android.view.ViewGroup
import com.xiaosw.api.util.ScreenUtil

/**
 * ClassName: [SingleFloatWindowLayoutTouchDelegate]
 * Description:
 *
 * Create by X at 2022/03/02 15:37.
 */
internal class SingleFloatWindowLayoutTouchDelegate : FloatWindowLayoutTouchDelegate {

    private var isAttach = false
    private lateinit var target: FloatWindowLayout<*>
    private lateinit var params: ViewGroup.LayoutParams
    private lateinit var context: Context

    private var mLastRawX = 0f
    private var mLastRawY = 0f
    private var isDrag = false
    private var mScreenWidth = 0

    override fun attach(target: FloatWindowLayout<*>) {
        if (isAttach) {
            return
        }
        this.target = target
        params = target.layoutParams as ViewGroup.LayoutParams
        context = target.context
        mScreenWidth = ScreenUtil.getScreenWidth(context)
        isAttach = true
    }

    override fun handleDispatchTouchEvent(event: MotionEvent) : Boolean {
        val rawX = event.rawX
        val rawY = event.rawY
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mLastRawX = rawX
                mLastRawY = rawY
                isDrag = false
            }

            MotionEvent.ACTION_MOVE -> {
                val moveX: Float = rawX - mLastRawX
                val moveY: Float = rawY - mLastRawY
                if (moveX != 0F || moveY != 0F) {
                    if (target.onDrag(moveX, moveY)) {
                        isDrag = true
                    }
                }
                mLastRawX = rawX
                mLastRawY = rawY
            }

            MotionEvent.ACTION_UP -> {
                touchUpLocked()
                if (isDrag) {
                    return true
                }
            }
        }
        return false
    }

    private fun touchUpLocked() {
        val selfCX = target.x + target.width / 2
        val to = if (mScreenWidth / 2 > selfCX) {
            0f
        } else {
            (mScreenWidth - target.width) * 1F
        }
        with(ObjectAnimator.ofFloat(target.x, to)) {
            duration = target.upAnimDuration
            interpolator = target.upAnimInterceptor
            addUpdateListener {
                target.x = it.animatedValue as Float
            }
            start()
        }
    }

}