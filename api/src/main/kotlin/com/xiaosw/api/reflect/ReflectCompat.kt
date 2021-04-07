package com.xiaosw.api.reflect

import androidx.annotation.Keep
import com.xiaosw.api.extend.tryCatch
import com.xiaosw.api.reflect.compat.ReflectCompatDelegate
import com.xiaosw.api.reflect.compat.ReflectCompatFactory
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * ClassName: [ReflectCompat]
 * Description:
 *
 * Create by xsw at 2021/04/07 10:34.
 */
@Keep
object ReflectCompat : ReflectCompatDelegate {

    private const val TAG = "ReflectCompat"

    private val mReflectDelegate by lazy {
        ReflectCompatFactory().create()
    }

    init {
        compat()
    }

    override fun compat() = mReflectDelegate.compat()

    fun forName(className: String): Class<*>? {
        return Class.forName(className)
    }

    fun getField(clazz: Class<*>, fieldName: String, isAccessible: Boolean = true) = tryCatch {
        clazz.getDeclaredField(fieldName)?.also {
            it.isAccessible = isAccessible
        }
    }

    fun setField(field: Field, value: Any? = null, targetObj: Any? = null) = tryCatch(def = false) {
        field.set(targetObj, value)
        true
    }

    fun getMethod(
        clazz: Class<*>,
        methodName: String,
        isAccessible: Boolean = true,
        vararg values: Class<*>
    ) = tryCatch {
        if (values != null) {
            clazz.getDeclaredMethod(methodName, *values)
        } else {
            clazz.getDeclaredMethod(methodName);
        }?.also {
            it.isAccessible = isAccessible
        }
    }

    fun invokeStaticMethod(method: Method, vararg values: Any?): Any? {
        return invokeMethod(method, null, *values)
    }

    fun invokeMethod(method: Method, targetObj: Any? = null, vararg values: Any?) =
        tryCatch {
            if (null == values || values.isEmpty()) {
                method.invoke(targetObj)
            } else {
                method.invoke(targetObj, *values)
            }
        }

}