package com.doudou.component.util

/**
 * ClassName: [Dispatcher]
 * Description:
 *
 * Create by X at 2022/06/24 11:39.
 */
interface Dispatcher {

    fun postToMainThread(delayMillis: Long = 0L, run: Runnable)

    fun removeCallback(runnable: Runnable?)

    fun removeAllCallbacksAndMessages()

}