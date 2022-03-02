package com.xiaosw.api.floating.internal

import android.animation.ObjectAnimator
import android.content.Context
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import com.xiaosw.api.util.ScreenUtil

/**
 * ClassName: [GlobalFloatWindowTouchDelegate]
 * Description:
 *
 * Create by X at 2022/03/02 15:37.
 */
internal class GlobalFloatWindowTouchDelegate {
    private val mLocationOnScreen by lazy {
        IntArray(2)
    }

    private var isAttach = false
    private lateinit var target: GlobalFloatWindowLayout
    private lateinit var params: WindowManager.LayoutParams
    private lateinit var context: Context
    private var mScreenCX = 0
    private lateinit var wm: WindowManager

    private var mLastRawX = 0f
    private var mLastRawY = 0f
    private var isDrag = false

    fun attach(target: GlobalFloatWindowLayout) {
        if (isAttach) {
            return
        }
        this.target = target
        params = target.layoutParams as WindowManager.LayoutParams
        context = target.context
        wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mScreenCX = ScreenUtil.getScreenWidth(context) / 2
        isAttach = true
    }

    fun handleDispatchTouchEvent(event: MotionEvent) : Boolean {
        val params = target.layoutParams as WindowManager.LayoutParams
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
                    params.x += moveX.toInt()
                    params.y += moveY.toInt()
                    wm.updateViewLayout(target, params)
                    isDrag = true
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
        target.getLocationOnScreen(mLocationOnScreen)
        val cx = mLocationOnScreen[0] + (target.width / 2)
        val to = if (mScreenCX > cx) {
            when {
                params.gravity and Gravity.LEFT != 0 -> {
                    0
                }
                params.gravity and Gravity.RIGHT != 0 -> {
                    -(ScreenUtil.getScreenWidth(context) - target.width)
                }
                else -> {
                    -(mScreenCX - target.width / 2)
                }
            }
        } else {
            when {
                params.gravity and Gravity.LEFT != 0 -> {
                    (ScreenUtil.getScreenWidth(context) - target.width)
                }
                params.gravity and Gravity.RIGHT != 0 -> {
                    0
                }
                else -> {
                    mScreenCX - target.width / 2
                }
            }
        }
        with(ObjectAnimator.ofInt(params.x, to)) {
            duration = target.upAnimDuration
            interpolator = target.upAnimInterceptor
            addUpdateListener {
                params.x = it.animatedValue as Int
                wm.updateViewLayout(target, params)
            }
            start()
        }
    }

}