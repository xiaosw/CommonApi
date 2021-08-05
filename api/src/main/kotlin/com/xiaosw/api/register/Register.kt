package com.xiaosw.api.register

/**
 * ClassName: [Register]
 * Description:
 *
 * Create by X at 2021/08/05 09:52.
 */
interface Register<T> {

    fun register(t: T) : Boolean

    fun unregister(t: T) : Boolean

    fun clear()

}