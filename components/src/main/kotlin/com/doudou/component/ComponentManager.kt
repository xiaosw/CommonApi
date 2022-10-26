package com.doudou.component

import androidx.annotation.Keep
import com.doudou.component.def.DefCommonParams
import com.doudou.component.params.CommonParams
import com.doudou.component.util.Standard
import com.doudou.component.util.impl.StandardImpl
import java.io.Serializable
import java.lang.RuntimeException

/**
 * ClassName: [ComponentManager]
 * Description:
 *
 * Create by X at 2022/06/24 09:56.
 */
@Keep
object ComponentManager : Serializable {

    private val mComponents by lazy {
        mutableMapOf<String, Any?>().also {
            DefCommonParams().inject()
            StandardImpl().inject()
        }
    }

    @JvmStatic
    fun <T> use(clazz: Class<T>) = use<T>(clazz.name)

    @JvmStatic
    fun <T> use(name: String) : T = mComponents[name] as T

    @JvmStatic
    fun <T> safeUse(clazz: Class<T>) = safeUse<T>(clazz.name)

    @JvmStatic
    fun <T> safeUse(name: String) : T? = mComponents[name] as? T

    @JvmStatic
    fun <T> register(clazz: Class<T>, impl: T) = register(clazz.name, impl)

    @JvmStatic
    fun <T> register(name: String, impl: T) {
        mComponents[name] = impl
    }
}