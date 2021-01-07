package com.xiaosw.api.util

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

    fun safe2Class(className: String?) = className?.tryCatch {
        Class.forName(className)
    } ?: null

    fun getDeclaredField(clazz: Class<*>?, filedName: String?, isAccessible: Boolean = true) : Field? {
        return clazz?.tryCatch {
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