package com.xiaosw.api.hook

import com.xiaosw.api.extend.tryCatch
import java.lang.reflect.Field

/**
 * @ClassName: [HookUtil]
 * @Description:
 *
 * Created by admin at 2021-01-06
 * @Email xiaosw0802@163.com
 */
object HookUtil {

    fun safe2Class(
        className: String?
        , showException: Boolean = true
    ) = className?.tryCatch(showException = showException) {
        Class.forName(className)
    } ?: null

    fun getDeclaredField(
        clazz: Class<*>?
        , filedName: String?
        , isAccessible: Boolean = true
        , showException: Boolean = true) : Field? {
        return clazz?.tryCatch(showException = showException) {
            it.getDeclaredField(filedName).also { field ->
                field.isAccessible = isAccessible
            }
        } ?: null
    }

    fun get(filed: Field?, any: Any? = null, isAccessible: Boolean = true) : Any? {
        return filed?.tryCatch {
            it.isAccessible = isAccessible
            it.get(any)
        } ?: null
    }

}