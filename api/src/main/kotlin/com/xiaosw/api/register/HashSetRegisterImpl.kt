package com.xiaosw.api.register

/**
 * ClassName: [HashSetRegisterImpl]
 * Description:
 *
 * Create by X at 2021/08/05 09:55.
 */
internal class HashSetRegisterImpl<T> : RegisterDelegate<T>() {

    private val mSet by lazy {
        HashSet<T>()
    }

    override fun contains(t: T) = mSet.contains(t)

    override fun size() = mSet.size

    override fun internalRegisterLock(t: T) = mSet.add(t)

    override fun internalUnregisterLock(t: T) = mSet.remove(t)

    override fun internalForEachLock(block: (T) -> Unit) {
        mSet.forEach {
            block.invoke(it)
        }
    }

    override fun isEmpty() = mSet.isEmpty()

    override fun clear() = mSet.clear()
}