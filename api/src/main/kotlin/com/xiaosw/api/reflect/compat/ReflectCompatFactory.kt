package com.xiaosw.api.reflect.compat

import android.os.Build

/**
 * ClassName: [ReflectCompatFactory]
 * Description:
 *
 * Create by xsw at 2021/04/07 11:25.
 */
internal class ReflectCompatFactory {

    fun create() : ReflectCompatDelegate {
        if (limitReflectAllApi()) {
            return LimitAllCompat()
        }
        if (limitReflectHiddenApi()) {
            return LimitHiddenCompat()
        }
        return DefaultCompat()
    }

    /**
     * 是否限制反射 @Hidden API
     */
    private inline fun limitReflectHiddenApi() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

    /**
     * 是否限制反射所有 API
     */
    private inline fun limitReflectAllApi() : Boolean {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            return false
        }
        // 后续版本如果禁止反射所有 API, 这里需要返回 false
        return false
    }
}