package com.xiaosw.api.reflect.compat

/**
 * ClassName: [DefaultCompat]
 * Description:
 *
 * Create by xsw at 2021/04/07 11:25.
 */
internal class DefaultCompat : ReflectCompatDelegate() {

    override fun compat(className: String) = true

    override fun compat(clazz: Class<*>) = true

}