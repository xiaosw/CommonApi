package com.xiaosw.api.util

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import com.xiaosw.api.AndroidContext
import com.xiaosw.api.extend.tryCatch

/**
 * @ClassName [ScreenUtil]
 * @Description
 *
 * @Date 2019-05-07.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
object ScreenUtil {

    /**
     * @Method [ScreenUtil.getScreenWH]
     * @Description:     Get screen width
     * @param context
     * @return
     * @return int[]
     */
    @JvmStatic
    inline fun getScreenWH(context: Context = AndroidContext.get()): IntArray {
        return IntArray(2).also {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            with(DisplayMetrics()) {
                wm.defaultDisplay.getMetrics(this)
                it[0] = widthPixels
                it[1] = heightPixels
            }
        }
    }

    @JvmStatic
    inline fun getScreenWidth(context: Context = AndroidContext.get()): Int {
        return getScreenWH(context)[0]
    }

    @JvmStatic
    inline fun getScreenHeight(context: Context = AndroidContext.get()): Int {
        return getScreenWH(context)[1]
    }

    /**
     * Take screen resolution
     *
     * @param activity
     *
     * @return Actual screen resolution "width*height" format
     */
    @JvmStatic
    inline fun getScreenResolution(activity: Activity?): String {
        var screenResolution = ""
        if (activity != null) {
            val screenWH = getScreenWH(activity)
            screenResolution = screenWH[0].toString() + "*" + screenWH[1]
        }
        return screenResolution
    }

    /**
     * @Method getStatusHeight
     * @Description:     Get the height of the status bar
     * @param context
     * @return int Status bar height
     */
    @JvmStatic
    inline fun getStatusHeight(context: Context = AndroidContext.get()): Int {
        return context?.tryCatch {
            // by properties
            it.resources.getIdentifier("status_bar_height",
                "dimen", "android").also { statusBarResId ->
                if (statusBarResId > 0) {
                    return@tryCatch it.resources.getDimensionPixelOffset(statusBarResId)
                }
            }

            (it as? Activity)?.let { activity ->
                // by top
                val rect = Rect().also {
                    activity.window.decorView.getWindowVisibleDisplayFrame(it)
                }
                if (rect.top > 0) {
                    return@tryCatch rect.top
                }

                // screen height - app height
                with(DisplayMetrics()) {
                    activity.windowManager.defaultDisplay.getMetrics(this)
                    return@tryCatch heightPixels - rect.height()
                }
            }
            return@tryCatch 0
        } ?: 0
    }

    @JvmStatic
    inline fun dp2px(context: Context = AndroidContext.get(), dp: Float) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)

    @JvmStatic
    inline fun dp2sp(context: Context = AndroidContext.get(), dp: Float) : Float {
        return px2sp(
            context,
            dp2px(context, dp)
        )
    }

    @JvmStatic
    inline fun px2dp(context: Context = AndroidContext.get(), px: Float) : Float {
        return px / context.resources.displayMetrics.density + 0.5f
    }

    @JvmStatic
    inline fun sp2px(context: Context = AndroidContext.get(), sp: Float) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.resources.displayMetrics)

    @JvmStatic
    inline fun px2sp(context: Context = AndroidContext.get(), px: Float) : Float {
        return px / context.resources.displayMetrics.scaledDensity + 0.5f
    }

    @JvmStatic
    inline fun sp2dp(context: Context = AndroidContext.get(), sp: Float) : Float {
        return px2dp(
            context,
            sp2px(context, sp)
        )
    }

    /**
     * Set up full-screen display
     * @param activity
     */
    inline fun setFullScreen(activity: Activity?) {
        activity?.tryCatch("setFullScreen") {
            val params = it.window.attributes
            params.flags = params.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
            it.window.attributes = params
            it.window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    /**
     * Cancel full-screen display
     * @param activity
     */
    @JvmStatic
    inline fun cancelFullScreen(activity: Activity?) {
        activity?.tryCatch("cancelFullScreen") {
            val params = it.window.attributes
            params.flags = params.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
            it.window.attributes = params
            it.window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    /**
     * Get whether it exists NavigationBar
     * @param context
     * @return true|false
     */
    @JvmStatic
    inline fun checkDeviceHasNavigationBar(context: Context): Boolean {
        var hasNavigationBar = false
        context.tryCatch("checkDeviceHasNavigationBar") {
            val rs = context.resources
            val id = rs.getIdentifier("config_showNavigationBar", "bool", "android")
            if (id > 0) {
                hasNavigationBar = rs.getBoolean(id)
            }
            val systemPropertiesClass = Class.forName("android.os.SystemProperties")
            val m = systemPropertiesClass.getMethod("get", String::class.java)
            val navBarOverride = m.invoke(systemPropertiesClass, "qemu.hw.mainkeys") as String
            if ("1" == navBarOverride) {
                hasNavigationBar = false
            } else if ("0" == navBarOverride) {
                hasNavigationBar = true
            }
        }
        return hasNavigationBar

    }

    /**
     * Get virtual function key height
     */
    @JvmStatic
    inline fun getVirtualBarHeight(context: Context): Int {
        var vh = 0
        tryCatch("getVirtualBarHeight") {
            if (checkDeviceHasNavigationBar(context)) {
                val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val display = windowManager.defaultDisplay
                with(DisplayMetrics()) {
                    val c = Class.forName("android.view.Display")
                    val method = c.getMethod("getRealMetrics", DisplayMetrics::class.java)
                    method.invoke(display, this)
                    vh = heightPixels - windowManager.defaultDisplay.height
                }
            }
        }
        return vh
    }

}