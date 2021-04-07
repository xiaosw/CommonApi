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
        if (limitReflectHiddenApi()) {
            return LimitHiddenCompat()
        }
        return DefaultCompat()
    }

    /**
     * 是否限制反射 @Hidden API
     */
    private inline fun limitReflectHiddenApi() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
}