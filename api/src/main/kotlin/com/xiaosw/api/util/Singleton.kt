package com.xiaosw.api.util

/**
 * ClassName: [Singleton]
 * Description:
 *
 * Create by xsw at 2021/04/01 16:54.
 */
abstract class Singleton<T> {

    @Volatile
    private var mInstance: T? = null

    protected abstract fun create() : T?

    fun get() : T {
        if (null == mInstance) {
            synchronized(this) {
                if (null == mInstance) {
                    mInstance = create()
                }
            }
        }
        return mInstance!!
    }
}