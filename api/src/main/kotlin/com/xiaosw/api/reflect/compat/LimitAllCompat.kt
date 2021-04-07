package com.xiaosw.api.reflect.compat

import com.xiaosw.api.extend.tryCatch
import com.xiaosw.api.reflect.ReflectCompat

/**
 * ClassName: [LimitAllCompat]
 * Description:
 *
 * Create by xsw at 2021/04/07 11:14.
 */
internal class LimitAllCompat : ReflectCompatDelegate() {

    override fun compat(className: String) = ReflectCompat.forName(className)?.let {
        compat(it)
    } ?: false

    override fun compat(clazz: Class<*>) : Boolean {
        tryCatch {
            if (replaceClassLoader(clazz)) {
                return true
            }
        }
        tryCatch {
            ReflectCompat.getField(clazz, "classLoader")?.let {
                val classLoaderOffset = UnsafeHelper.objectFieldOffset(it)
                UnsafeHelper.putObject(clazz, classLoaderOffset, null)
                return true
            }
        }
        return false
    }
}