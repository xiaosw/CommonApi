package com.doudou.log.internal

import com.doudou.log.annotation.Level

/**
 * ClassName: [ILog]
 * Description:
 *
 * Create by X at 2021/11/12 16:57.
 */
internal interface ILog {

    @Level val level: Int
    val enable: Boolean

    fun findTag(ignoreDisable: Boolean = false) : String
    fun println(message: String?, isError: Boolean = false)
    fun println(messageProvider: () -> String?, isError: Boolean = false)

    fun v(tag: String?, message: String?, th: Throwable?)
    fun v(tag: String?, messageProvider: () -> String?, th: Throwable?)

    fun d(tag: String?, message: String?, th: Throwable?)
    fun d(tag: String?, messageProvider: () -> String?, th: Throwable?)

    fun i(tag: String?, message: String?, th: Throwable?)
    fun i(tag: String?, messageProvider: () -> String?, th: Throwable?)

    fun w(tag: String?, message: String?, th: Throwable?)
    fun w(tag: String?, messageProvider: () -> String?, th: Throwable?)

    fun e(tag: String?, message: String?, th: Throwable?)
    fun e(tag: String?, messageProvider: () -> String?, th: Throwable?)

}