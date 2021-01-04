package com.xiaosw.api.extend

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.FragmentActivity
import com.xiaosw.api.util.ScreenUtil
import com.xiaosw.api.util.ToastUtil

/**
 * @ClassName [Context]
 * @Description
 *
 * @Date 2019-04-26.
 * @Author xiaosw<xiaosw0802@163.com>.
 */

/**
 * @ClassName {@link Context}
 * @Description
 *
 * @Date 2018-05-25.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
inline fun Context.dp2px(dp: Float) = ScreenUtil.dp2px(this, dp)
inline fun Context.dp2sp(dp: Float) = ScreenUtil.dp2sp(this, dp)
inline fun Context.px2dp(px: Float) = ScreenUtil.px2dp(this, px)
inline fun Context.sp2px(sp: Float) = ScreenUtil.sp2px(this, sp)
inline fun Context.sp2dp(sp: Float) = ScreenUtil.sp2dp(this, sp)
inline fun Context.px2sp(px: Float) = ScreenUtil.px2sp(this, px)

inline fun Context.getScreenWH() = ScreenUtil.getScreenWH(this)

inline fun Context.getStatusBarHeight() = ScreenUtil.getStatusHeight()

inline fun Context.showToast(message: CharSequence,
                                    duration: Int = Toast.LENGTH_SHORT)
        = ToastUtil.showToast(this, message, duration)

inline  fun Context.showToast(textId: Int,
                                    duration: Int = Toast.LENGTH_SHORT)
        = ToastUtil.showToast(this, textId, duration)

fun Context?.checkSelfPermissionCompat(vararg permissions: String) : Boolean {
    this?.let {
        val isM = applicationInfo.targetSdkVersion >= Build.VERSION_CODES.M
        for (permission in permissions) {
            if (permission.isNull(true)) {
                continue
            }
            if (isM) {
                if (ContextCompat.checkSelfPermission(this, permission)
                            != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            } else if (PermissionChecker.checkSelfPermission(this, permission)
                != PermissionChecker.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
    return false
}

fun Context?.findActivity() : Activity? {
    if (isNull()) {
        return null
    }
    if (this is Activity) {
        return this
    }
    if (this is ContextWrapper) {
        return baseContext.findActivity()
    }
    return null
}

fun Context?.isDestroyed() : Boolean {
    val activity = findActivity()
    if (activity.isNull()) {
        return true
    }
    var isDestroyed = false
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        isDestroyed = activity!!.isDestroyed
    }
    (activity as? FragmentActivity)?.let {
        isDestroyed = it.isDestroyed
    }
    return isDestroyed
}