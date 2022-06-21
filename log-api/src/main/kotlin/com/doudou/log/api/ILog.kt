package com.doudou.log.api

/**
 * ClassName: [ILog]
 * Description:
 *
 * Create by X at 2021/11/12 16:57.
 */
interface ILog {

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