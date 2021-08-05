package com.xiaosw.api.register

import com.xiaosw.api.extend.isNotNull
import java.util.*

/**
 * ClassName: [WeakHashMapRegisterImpl]
 * Description:
 *
 * Create by X at 2021/08/05 09:55.
 */
internal class WeakHashMapRegisterImpl<T> : RegisterDelegate<T>() {

    private val mWeak by lazy {
        WeakHashMap<T, Any?>()
    }

    override fun internalRegisterLock(t: T) = mWeak.put(t, null).let {
        true
    }

    override fun internalUnregisterLock(t: T) = mWeak.remove(t).isNotNull()

    override fun internalForEachLock(block: (T) -> Unit) = mWeak.keys.forEach {
        block.invoke(it)
    }

    override fun contains(t: T) = mWeak.contains(t)

    override fun size() = mWeak.size

    override fun clear() = mWeak.clear()

    override fun isEmpty() = mWeak.isEmpty()

}