package com.xiaosw.api.util

import android.util.SparseArray
import com.xiaosw.api.extend.isNull
import com.xiaosw.api.extend.tryCatch
import java.io.Closeable

/**
 * @ClassName: [Utils]
 * @Description:
 *
 * Created by admin at 2020-09-11
 * @Email xiaosw0802@163.com
 */
object Utils {

    @JvmStatic
    fun safeClose(vararg closeables: Closeable?) {
        closeables?.forEach {
            it?.tryCatch { closable ->
                closable.close()
            }
        }
    }

    @JvmStatic
    fun isEmpty(content: CharSequence?) = content.isNullOrEmpty()

    @JvmStatic
    fun isEmpty(collection: Collection<*>?) = collection?.isEmpty() ?: true

    @JvmStatic
    fun isEmpty(map: Map<*, *>?) = map?.isEmpty() ?: true

    @JvmStatic
    fun isEmpty(array: Array<*>?) = (array?.size ?: 0) > 0

    @JvmStatic
    fun isEmpty(array: SparseArray<*>?) = (array?.size() ?: 0) > 0

    @JvmStatic
    fun isEmpty(any: Any?) = any.isNull()

    @JvmStatic
    @JvmOverloads
    fun hasNull(vararg args: Any?, checkEmpty: Boolean = false) : Boolean {
        args?.forEach {
            if (it.isNull()) {
                return true
            }
            if (checkEmpty) {
                when(it) {
                    is Collection<*> -> if (isEmpty(it)) return true
                    is Map<*, *> -> if (isEmpty(it)) return true
                    is String -> if (isEmpty(it)) return true
                    is Array<*> -> if (isEmpty(it)) return true
                }
            }
        }
        return false
    }

}