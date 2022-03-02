package com.xiaosw.api.floating.internal

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.util.AttributeSet
import android.view.*
import android.view.animation.BounceInterpolator
import android.view.animation.Interpolator
import android.widget.FrameLayout
import com.xiaosw.api.AndroidContext
import com.xiaosw.api.floating.FloatWindowController
import com.xiaosw.api.floating.FloatWindowManager
import com.xiaosw.api.floating.OnFloatWindowVisibilityChangeListener
import com.xiaosw.api.manager.ActivityLifeManager
import com.xiaosw.api.register.RegisterDelegate

/**
 * ClassName: [GlobalFloatWindowLayout]
 * Description:
 *
 * Create by X at 2022/03/01 16:15.
 */
internal class GlobalFloatWindowLayout @JvmOverloads constructor(
    context: Context = AndroidContext.get(), attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr)
    , FloatWindowController
    , ActivityLifeManager.AppLifecycleListener {

    private val mOnFloatWindowVisibilityChangeListeners by lazy {
        RegisterDelegate.createWeak<OnFloatWindowVisibilityChangeListener>()
    }

    private var mFloatingState = FloatingState.INIT
        set(value) {
            if (field == value) {
                return
            }
            if (value == FloatingState.SHOWING) {
                mTouchDelegate.attach(this)
            }
            val old = field
            field = value
            internalVisibilityChange(old)
        }

    private val sWM by lazy {
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    private val mParams by lazy {
        WindowManager.LayoutParams().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
            gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            format = PixelFormat.TRANSLUCENT
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE
            flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        }
    }

    private val mTouchDelegate by lazy {
        GlobalFloatWindowTouchDelegate()
    }

    private var isOnlyAppForeground = true
    var upAnimDuration: Long = 500
    var upAnimInterceptor: Interpolator = BounceInterpolator()

    init {
        ActivityLifeManager.register(this)
    }

    override fun isShowing() = mFloatingState == FloatingState.SHOWING

    override fun register(t: OnFloatWindowVisibilityChangeListener) =
        mOnFloatWindowVisibilityChangeListeners.register(t)

    override fun unregister(t: OnFloatWindowVisibilityChangeListener) =
        mOnFloatWindowVisibilityChangeListeners.unregister(t)

    override fun clear() = mOnFloatWindowVisibilityChangeListeners.clear()

    override fun show(child: View) : FloatWindowController {
        if (isShowing()) {
            return this
        }
        if (!FloatWindowManager.canDrawOverlays()) {
            return this
        }
        setBackgroundColor(Color.GREEN)
        addView(child)
        sWM.addView(this, mParams)
        mFloatingState = FloatingState.SHOWING
        return this
    }

    private fun internalHide() {
        if (mFloatingState == FloatingState.SHOWING) {
            sWM.removeViewImmediate(this)
            mFloatingState = FloatingState.HIDE
        }
    }

    private fun internalShow() {
        if (mFloatingState == FloatingState.HIDE) {
            sWM.addView(this, mParams)
            mFloatingState = FloatingState.SHOWING
        }
    }

    override fun onlyAppForeground(onlyAppForeground: Boolean): FloatWindowController {
        isOnlyAppForeground = onlyAppForeground
        return this
    }

    override fun upAnimDuration(duration: Long): FloatWindowController {
        upAnimDuration = duration
        return this
    }

    override fun upAnimInterceptor(interceptor: Interpolator): FloatWindowController {
        upAnimInterceptor = interceptor
        return this
    }

    override fun dismiss() {
        if (!isShowing()) {
            return
        }
        removeAllViews()
        sWM.removeViewImmediate(this)
        mFloatingState = FloatingState.DISMISS
    }

    override fun onAppBackground(activeTime: Long) {
        if (isOnlyAppForeground) {
            internalHide()
        }
    }

    override fun onAppForeground(isFirstLauncher: Boolean) {
        if (isOnlyAppForeground && mFloatingState == FloatingState.HIDE) {
            internalShow()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val isHandle = ev?.let {
            mTouchDelegate.handleDispatchTouchEvent(ev)
        } ?: false
        return isHandle || super.dispatchTouchEvent(ev)
    }

    private fun internalVisibilityChange(oldState: FloatingState) {
        if (mOnFloatWindowVisibilityChangeListeners.isEmpty()) {
            return
        }
        if (oldState == FloatingState.HIDE ||
            (oldState != FloatingState.HIDE && mFloatingState != FloatingState.SHOWING)) {
            return
        }
        val isShowing = isShowing()
        mOnFloatWindowVisibilityChangeListeners.forEach {
            it.onFloatWindowVisibilityChange(isShowing)
        }
    }

    private enum class FloatingState {
        INIT,
        SHOWING,
        HIDE,
        DISMISS
    }
}