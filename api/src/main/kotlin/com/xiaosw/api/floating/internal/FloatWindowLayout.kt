package com.xiaosw.api.floating.internal

import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.view.animation.BounceInterpolator
import android.view.animation.Interpolator
import android.widget.FrameLayout
import com.xiaosw.api.AndroidContext
import com.xiaosw.api.floating.FloatWindowController
import com.xiaosw.api.floating.FloatWindowManager
import com.xiaosw.api.floating.OnFloatWindowVisibilityChangeListener
import com.xiaosw.api.register.RegisterDelegate
import com.xiaosw.api.storage.DataStorageManager

/**
 * ClassName: [FloatWindowLayout]
 * Description:
 *
 * Create by X at 2022/03/01 16:15.
 */
internal abstract class FloatWindowLayout<T : FloatWindowLayoutTouchDelegate> @JvmOverloads constructor(
    private val owner: Any
    , context: Context = AndroidContext.get()
    , attrs: AttributeSet? = null
    , defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr)
    , FloatWindowController {

    private val mOnFloatWindowVisibilityChangeListeners by lazy {
        RegisterDelegate.createWeak<OnFloatWindowVisibilityChangeListener>()
    }

    var mFloatingState = FloatingState.INIT
        private set(value) {
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

    private val mTouchDelegate by lazy {
        providerTouchDelegate()
    }

    protected var isOnlyAppForeground = true
        private set

    var upAnimDuration: Long = 500
    var upAnimInterceptor: Interpolator = BounceInterpolator()

    override fun isShowing() = mFloatingState == FloatingState.SHOWING

    override fun register(t: OnFloatWindowVisibilityChangeListener) =
        mOnFloatWindowVisibilityChangeListeners.register(t)

    override fun unregister(t: OnFloatWindowVisibilityChangeListener) =
        mOnFloatWindowVisibilityChangeListeners.unregister(t)

    override fun clear() = mOnFloatWindowVisibilityChangeListeners.clear()

    override fun owner() = owner

    override fun show(child: View) : Boolean {
        if (isShowing()) {
            return true
        }
        val isAddedToWindow = addFloatingToWindow()
        if (!isAddedToWindow) {
           return false
        }
        addView(child)
        mFloatingState = FloatingState.SHOWING
        return true
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
        mFloatingState = FloatingState.DISMISS
        FloatWindowManager.remove(owner, isGlobal())
        DataStorageManager.put(ownerKey(KEY_FLOATING_LOCATION_X), x)
        DataStorageManager.put(ownerKey(KEY_FLOATING_LOCATION_Y), y)
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

    protected fun setFloatingState(state: FloatingState) {
        mFloatingState = state
    }

    protected fun ownerKey(key: String) = "${key}_${owner.hashCode()}"

    abstract fun providerTouchDelegate() : T

    abstract fun addFloatingToWindow() : Boolean

    abstract fun onDrag(moveX: Float, moveY: Float) : Boolean

    abstract fun isGlobal() : Boolean

    internal companion object {
        const val KEY_FLOATING_LOCATION_X = "key_floating_location_x"
        const val KEY_FLOATING_LOCATION_Y = "key_floating_location_y"
    }

}