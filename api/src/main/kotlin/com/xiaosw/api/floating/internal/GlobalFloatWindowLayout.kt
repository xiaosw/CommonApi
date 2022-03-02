package com.xiaosw.api.floating.internal

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.util.AttributeSet
import android.view.*
import com.xiaosw.api.AndroidContext
import com.xiaosw.api.floating.FloatWindowManager
import com.xiaosw.api.manager.ActivityLifeManager

/**
 * ClassName: [GlobalFloatWindowLayout]
 * Description:
 *
 * Create by X at 2022/03/01 16:15.
 */
internal class GlobalFloatWindowLayout @JvmOverloads constructor(
    context: Context = AndroidContext.get(), attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FloatWindowLayout(context, attrs, defStyleAttr)
    , ActivityLifeManager.AppLifecycleListener {

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

    init {
        ActivityLifeManager.register(this)
    }

    override fun dismiss() {
        sWM.removeViewImmediate(this)
        super.dismiss()
    }

    override fun providerTouchDelegate() =
        GlobalFloatWindowLayoutTouchDelegate() as FloatWindowLayoutTouchDelegate<FloatWindowLayout>

    override fun addFloatingToWindow(): Boolean {
        if (!FloatWindowManager.canDrawOverlays()) {
            return false
        }
        sWM.addView(this, mParams)
        return true
    }

    override fun onDrag(moveX: Float, moveY: Float): Boolean {
        mParams.x += moveX.toInt()
        mParams.y += moveY.toInt()
        sWM.updateViewLayout(this, mParams)
        return true
    }

    override fun onAppBackground(activeTime: Long) {
        if (isOnlyAppForeground && isShowing()) {
            sWM.removeViewImmediate(this)
            setFloatingState(FloatingState.HIDE)
        }
    }

    override fun onAppForeground(isFirstLauncher: Boolean) {
        if (mFloatingState == FloatingState.HIDE) {
            sWM.addView(this, mParams)
            setFloatingState(FloatingState.SHOWING)
        }
    }
}