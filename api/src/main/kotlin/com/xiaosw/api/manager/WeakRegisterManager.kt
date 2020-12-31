package com.xiaosw.api.manager

import java.util.*

/**
 * @ClassName: [WeakRegisterManager]
 * @Description: 注意：使用时，请保证 t 至少被一个对象引用。
 *
 * Created by admin at 2020-12-31
 * @Email xiaosw0802@163.com
 */
class WeakRegisterManager<T> {

    private val mCallbacks by lazy {
        WeakHashMap<T?, Any?>()
    }

    fun register(t: T?) = register(t) {
        true
    }

    fun register(t: T?, filter: (T?) -> Boolean) {
        if (mCallbacks.contains(t)) {
            return
        }
        if (filter(t)) {
            mCallbacks[t] = null
        }
    }

    fun unregister(t: T?) {
        if (!mCallbacks.contains(t)) {
            return
        }
        mCallbacks.remove(t)
    }

    fun clear() {
        mCallbacks.clear()
    }

    fun forEach(block: (T) -> Unit) {
        if (mCallbacks.isEmpty()) {
            return
        }
        mCallbacks.entries?.forEach {
            it.key?.run {
                block(this)
            }
        }
    }

}