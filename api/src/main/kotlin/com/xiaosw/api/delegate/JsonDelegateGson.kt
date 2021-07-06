package com.xiaosw.api.delegate

import com.xiaosw.api.wrapper.GsonWrapper
import java.lang.reflect.Type

/**
 * ClassName: [JsonDelegateGson]
 * Description:
 *
 * Create by X at 2021/07/02 16:33.
 */
class JsonDelegateGson : JsonDelegate() {

    override fun toJson(any: Any) :String {
        if (any is String) {
            return any
        }
        return GsonWrapper.toJson(any)
    }

    override fun <T> fromJson(json: String, clazz: Class<T>) : T? {
        if (clazz == String::class.java) {
            return json as T
        }
        return if (GsonWrapper.isJson(json)) {
            GsonWrapper.fromJson(json, clazz)
        } else null
    }

    override fun <T> fromJson(json: String, type: Type) : T? {
        if (type == String::class.java) {
            return json as T
        }
        return if (GsonWrapper.isJson(json)) {
            GsonWrapper.fromJson(json, type)
        } else null
    }
}