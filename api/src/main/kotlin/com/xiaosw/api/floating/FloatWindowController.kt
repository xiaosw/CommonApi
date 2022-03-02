package com.xiaosw.api.floating

import android.view.View
import android.view.animation.Interpolator
import com.xiaosw.api.register.Register

/**
 * ClassName: [FloatWindowController]
 * Description:
 *
 * Create by X at 2022/03/01 16:09.
 */
interface FloatWindowController : Register<OnFloatWindowVisibilityChangeListener> {

    fun show(view: View) : FloatWindowController

    fun dismiss()

    fun isShowing() : Boolean

    fun onlyAppForeground(onlyAppForeground: Boolean) : FloatWindowController

    fun upAnimDuration(duration: Long = 500L) : FloatWindowController

    fun upAnimInterceptor(interceptor: Interpolator) : FloatWindowController

}