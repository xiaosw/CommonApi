package com.xiaosw.api.register

/**
 * ClassName: [ArrayListRegisterImpl]
 * Description:
 *
 * Create by X at 2021/08/05 09:55.
 */
internal class ArrayListRegisterImpl<T> : RegisterDelegate<T>() {

    private val mArrayList by lazy {
        ArrayList<T>()
    }

    override fun contains(t: T) = mArrayList.contains(t)

    override fun size() = mArrayList.size

    override fun internalRegisterLock(t: T) = mArrayList.add(t)

    override fun internalUnregisterLock(t: T) = mArrayList.remove(t)

    override fun internalForEachLock(block: (T) -> Unit) {
        mArrayList.forEach {
            block.invoke(it)
        }
    }

    override fun isEmpty() = mArrayList.isEmpty()

    override fun clear() = mArrayList.clear()
}