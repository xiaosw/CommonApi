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
fun Class<*>?.safeDeclaredMethod(
    methodName: String
    , isAccessible: Boolean = true
    , showException: Boolean = true
    , vararg args: Class<*>) = this?.tryCatch(showException = showException) {
    it.getDeclaredMethod(methodName, *args).also { method ->
        method.isAccessible = isAccessible
    }
} ?: null

fun Class<*>?.safeDeclaredField(
    filedName: String?
    , isAccessible: Boolean = true
    , showException: Boolean = true) = this?.tryCatch(showException = showException) {
    it.getDeclaredField(filedName).also { field ->
        field.isAccessible = isAccessible
    }
} ?: null

fun Field?.safeGet(any: Any? = null, isAccessible: Boolean = true) = this?.tryCatch {
    it.isAccessible = isAccessible
    it.get(any)
} ?: null

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
        , showException: Boolean = true) = clazz.safeDeclaredField(filedName, isAccessible, showException)

    fun get(filed: Field?, any: Any? = null, isAccessible: Boolean = true) = filed.safeGet(any, isAccessible)

}