package com.xiaosw.api.reflect.compat

import java.lang.Exception

/**
 * ClassName: [ReflectCompatDelegate]
 * Description:
 *
 * Create by xsw at 2021/04/07 10:41.
 */
abstract class ReflectCompatDelegate {

    protected inline fun replaceClassLoader(clazz: Class<*>) = try {
        Class::class.java.getDeclaredField("classLoader")?.let {
            it.isAccessible = true
            it.set(clazz, null)
        }
        true
    } catch (e: Exception) {
        false
    }

    abstract fun compat(className: String) : Boolean

    abstract fun compat(clazz: Class<*>) : Boolean

}