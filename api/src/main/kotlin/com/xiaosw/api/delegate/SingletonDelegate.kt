package com.xiaosw.api.delegate

/**
 * ClassName: [SingletonDelegate]
 * Description:
 *
 * Create by X at 2021/07/02 17:36.
 */
abstract class SingletonDelegate<T> {

    protected constructor()

    private var mInstance: T? = null

    protected abstract fun create() : T

    fun get(): T {
        if (mInstance == null) {
            synchronized(this) {
                if (mInstance == null) {
                    mInstance = create()
                }
            }
        }
        return mInstance!!
    }
}