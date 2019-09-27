package com.xiaosw.api.util

import android.annotation.TargetApi
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.xiaosw.api.extend.hasKitkat
import com.xiaosw.api.extend.hasLollipop
import com.xiaosw.api.extend.statusBarHeight

/**
 * @ClassName [StatusBarUtil]
 * @Description
 *
 * @Date 2019-04-26.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
object StatusBarUtil {
    private const val TAG = "StatusBarUtil"

    enum class DeviceMode(value: Int) {
        NONE(0),
        MIUI(1),
        FLYME(2),
        ANDROID(3)
    }

    /**
     * Modify the status bar to full transparency
     *
     * @param activity
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun transparencyStatusBar(activity: Activity) {
        activity.window.apply {
            when {
                // 5.0+
                hasLollipop() -> {
                    clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    statusBarColor = Color.TRANSPARENT
                }

                // 4.4+
                hasKitkat() -> {
                    setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                }
            }
        }

    }

    /**
     * Modify the status bar color, support 4.4 or later
     *
     * @param activity
     * @param colorId
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun setStatusBarColor(activity: Activity, colorId: Int) {
        activity.window.apply {
            when {
                // 5.0+
                hasLollipop() -> {
                    statusBarColor = ContextCompat.getColor(activity, colorId)
                }

                // 4.4 +
                hasKitkat() -> {
                    addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    (activity.findViewById<View>(android.R.id.content) as? ViewGroup)?.let {
                        it.getChildAt(0)?.also {
                            it.fitsSystemWindows = true
                        }

                        it.addView(View(activity).apply {
                            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                activity.statusBarHeight())
                            setBackgroundColor(colorId)
                        }, 0)
                    }
                }
            }
        }
    }

    /**
     * Set status bar black font icon，
     * Adapter version 4.4 and above MIUIV, Flyme and other Android versions above 6.0
     *
     * @param activity
     * @return [DeviceMode]
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun setStatusBarLightMode(activity: Activity): DeviceMode {
        var result = DeviceMode.NONE
        if (hasKitkat()) {
            activity.window.apply {
                when {
                    miuiStatusBarMode(this, true) ->
                        result = DeviceMode.MIUI

                    flymeSetStatusBarLightMode(this, true) ->
                        result = DeviceMode.FLYME

                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        result = DeviceMode.ANDROID
                    }
                }
            }
        }
        return result
    }

    /**
     * Set status bar black font icons when the system type is known。
     * Adapter version 4.4 and above MIUIV, Flyme and other Android versions above 6.0
     *
     * @param activity
     * @param deviceMode [DeviceMode]
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun setStatusBarLightMode(activity: Activity, deviceMode: DeviceMode) {
        when (deviceMode) {
            DeviceMode.MIUI ->
                miuiStatusBarMode(activity.window, true)
            DeviceMode.FLYME ->
                flymeSetStatusBarLightMode(activity.window, true)
            DeviceMode.ANDROID ->
                activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

    }

    /**
     * Set status bar light font icon，
     * Adapter version 4.4 and above MIUIV, Flyme and other Android versions above 6.0
     *
     * @param activity
     * @return [DeviceMode]
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun setStatusBarDarkMode(activity: Activity): DeviceMode {
        var result = DeviceMode.NONE
        if (hasKitkat()) {
            activity.window.apply {
                when {
                    miuiStatusBarMode(this, false) ->
                        result = DeviceMode.MIUI

                    flymeSetStatusBarLightMode(this, false) ->
                        result = DeviceMode.FLYME

                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                        result = DeviceMode.ANDROID
                    }
                }
            }
        }
        return result
    }

    /**
     * @param deviceMode
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun setStatusBarDarkMode(activity: Activity, deviceMode: DeviceMode) {
        if (hasKitkat()) {
            when (deviceMode) {
                DeviceMode.MIUI ->
                    miuiStatusBarMode(activity.window, false)

                DeviceMode.FLYME ->
                    flymeSetStatusBarLightMode(
                        activity.window,
                        false
                    )

                DeviceMode.ANDROID ->
                    activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }
        }
    }


    /**
     * Set the status bar icon to dark and Meizu specific text style
     * Can be used to determine if it is a Flyme user
     *
     * @param window Need to set the window
     * @param dark   Whether to set the status bar font and icon color to dark
     * @return boolean true: success
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private inline fun flymeSetStatusBarLightMode(window: Window?, dark: Boolean): Boolean {
        if(hasKitkat()) {
            window?.apply {
                try {
                    val lp = attributes
                    val darkFlag = WindowManager.LayoutParams::class.java
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
                    val meizuFlags = WindowManager.LayoutParams::class.java
                        .getDeclaredField("meizuFlags")
                    darkFlag.isAccessible = true
                    meizuFlags.isAccessible = true
                    val bit = darkFlag.getInt(null)
                    var value = meizuFlags.getInt(lp)
                    value = if (dark) {
                        // android 6.0 or above.
                        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                        clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        value or bit
                    } else {
                        value and bit.inv()
                    }
                    meizuFlags.setInt(lp, value)
                    attributes = lp
                    return true
                } catch (e: Exception) {
                    // No thing
                }
            }
        }
        return false
    }

    /**
     * Set the status bar font icon to dark and require MIUI V6 or above
     *
     * @param window Need to set the window
     * @param isDark   Whether to set the status bar font and icon color to dark
     * @return boolean true: success
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private inline fun miuiStatusBarMode(window: Window?, isDark: Boolean): Boolean {
        if (hasKitkat()) {
            window?.apply {
                try {
                    val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
                    val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
                    val darkModeFlag = field.getInt(layoutParams)
                    val extraFlagField = javaClass.getMethod("setExtraFlags", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
                    if (isDark) {
                        // low version
                        extraFlagField.invoke(window, darkModeFlag, darkModeFlag)//Status bar transparent and black font

                        // android 6.0 or above; miui 7.7.13 or above.
                        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                        clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    } else {
                        extraFlagField.invoke(window, 0, darkModeFlag)//Clear black font
                    }
                    return true
                } catch (e: Exception) {
                    // LogUtil.e(TAG, "miuiStatusBarMode: ", e)
                    // No thing
                }
            }
        }
        return false
    }
}