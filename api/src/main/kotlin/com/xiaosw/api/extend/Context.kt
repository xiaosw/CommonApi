package com.xiaosw.api.extend

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import com.xiaosw.api.util.ScreenUtil
import com.xiaosw.api.util.ToastUtil

/**
 * @ClassName [Context]
 * @Description
 *
 * @Date 2019-04-26.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
inline fun Context.checkSelfPermissionCompat(permission: String) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    checkSelfPermission(permission)
} else {
    PackageManager.PERMISSION_GRANTED
}

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

fun Context.checkPermissionCompat(vararg permissions: String) : Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        for (permission in permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
    return true
}