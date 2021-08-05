package com.xiaosw.api.register

/**
 * @ClassName: [RegisterDelegate]
 * @Description:
 *
 * Created by admin at 2020-12-31
 * @Email xiaosw0802@163.com
 */
abstract class RegisterDelegate<T> : Register<T> {

    override fun register(t: T) : Boolean {
        if (contains(t)) {
            return false
        }
        return synchronized(this) {
            internalRegisterLock(t)
        }
    }

    override fun unregister(t: T) : Boolean {
        return if (contains(t)) {
            synchronized(this) {
                internalUnregisterLock(t)
            }
        } else false
    }

    fun forEach(block: (T) -> Unit) {
        if (isEmpty()) {
            return
        }
        internalForEachLock(block)
    }

    @Throws(Throwable::class)
    protected fun finalize() = clear()

    abstract fun contains(t: T) : Boolean

    abstract fun size() : Int

    abstract fun isEmpty() : Boolean

    internal abstract fun internalRegisterLock(t: T) : Boolean

    internal abstract fun internalUnregisterLock(t: T) : Boolean

    internal abstract fun internalForEachLock(block: (T) -> Unit)

    companion object {

        fun <T> createWeak() : RegisterDelegate<T> = WeakHashMapRegisterImpl()

        fun <T> createHashSet() : RegisterDelegate<T>  = HashSetRegisterImpl()

        fun <T> createArrayList() : RegisterDelegate<T>  = ArrayListRegisterImpl()
    }

}