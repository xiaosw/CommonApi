package com.xiaosw.api.delegate

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * ClassName: [JsonDelegate]
 * Description:
 *
 * Create by X at 2021/07/02 16:25.
 */
abstract class JsonDelegate {

    @JvmOverloads
    inline fun <reified T> parseType(clazz: Class<T>, index: Int = 0) : Type? {
        val interfacesSize = clazz.genericInterfaces?.size ?: 0
        var type = if (clazz.isInterface || T::class.java.isInterface) {
            if (interfacesSize > index) clazz.genericInterfaces[index] as? ParameterizedType else null
        } else {
            (clazz.genericSuperclass as? ParameterizedType)?.let {
                it
            } ?: if (interfacesSize > index) (clazz.genericInterfaces[index] as? ParameterizedType) else null
        }
        return (type?.actualTypeArguments)?.let {
            if (it.isEmpty()) null else it[0]
        } ?: null
    }

    /**
     * 转 json 格式字符串
     */
    abstract fun toJson(any: Any) : String

    /**
     * json 转 java 对象
     */
    abstract fun <T> fromJson(json: String, clazz: Class<T>) : T?

    /**
     * json 转 java 对象
     */
    abstract fun <T> fromJson(json: String, type: Type) : T?

    companion object {
        val DEF by lazy {
            JsonDelegateGson()
        }
    }
}