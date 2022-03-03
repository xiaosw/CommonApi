package com.xiaosw.api.floating.internal

import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import com.doudou.log.loge
import com.xiaosw.api.AndroidContext
import com.xiaosw.api.extend.isDestroyedCompat
import com.xiaosw.api.extend.optionalImpl
import com.xiaosw.api.floating.FloatWindowController
import com.xiaosw.api.manager.ActivityLifeManager
import com.xiaosw.api.storage.DataStorageManager

/**
 * ClassName: [SingleFloatWindowLayout]
 * Description:
 *
 * Create by X at 2022/03/01 16:15.
 */
internal class SingleFloatWindowLayout @JvmOverloads constructor(
    owner: Any
    , context: Context = AndroidContext.get()
    , attrs: AttributeSet? = null
    , defStyleAttr: Int = 0
) : FloatWindowLayout<SingleFloatWindowLayoutTouchDelegate>(owner, context, attrs, defStyleAttr)
    , FloatWindowController {

    private var mAttachActivity: Activity? = null
    private var mWindowWidth = 0f
    private var mWindowHeight = 0f

    private val mActivityLifeListener = object : Application.ActivityLifecycleCallbacks by optionalImpl() {

        override fun onActivityDestroyed(activity: Activity) {
            if (mAttachActivity == activity) {
                dismiss()
            }
        }

    }

    override fun providerTouchDelegate() = SingleFloatWindowLayoutTouchDelegate()

    override fun addFloatingToWindow(): Boolean {
        if (parent is ViewGroup) {
            return false
        }
        return ActivityLifeManager.topActivity()?.let {
            if (it.isDestroyedCompat()) {
                return false
            }
            mAttachActivity = it
            AndroidContext.get().registerActivityLifecycleCallbacks(mActivityLifeListener)
            with(it.window.decorView.findViewById<FrameLayout>(android.R.id.content)) {
                post {
                    mWindowWidth = measuredWidth * 1F
                    mWindowHeight = measuredHeight * 1F
                }
                val params = LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )
                val oldX = DataStorageManager.getFloat(ownerKey(KEY_FLOATING_LOCATION_X), Float.MIN_VALUE)
                val oldY = DataStorageManager.getFloat(ownerKey(KEY_FLOATING_LOCATION_Y), Float.MIN_VALUE)
                if (oldX != Float.MIN_VALUE || oldY != Float.MIN_VALUE) {
                    this@SingleFloatWindowLayout.x = oldX
                    this@SingleFloatWindowLayout.y = oldY
                } else {
                    params.gravity = Gravity.CENTER_VERTICAL or Gravity.LEFT
                }
                addView(this@SingleFloatWindowLayout, params)
            }
            true
        } ?: false
    }

    override fun onDrag(moveX: Float, moveY: Float): Boolean {
        val newX = x + moveX
        val newY = y + moveY
        x = newX.coerceAtLeast(0f).coerceAtMost(mWindowWidth - width)
        y = newY.coerceAtLeast(0f).coerceAtMost(mWindowHeight - height)
        return true
    }

    override fun dismiss() {
        AndroidContext.get().unregisterActivityLifecycleCallbacks(mActivityLifeListener)
        super.dismiss()
    }

    override fun isGlobal() = false

}