package com.doudou.log.internal

import com.doudou.log.api.ILog

/**
 * ClassName: [LogN]
 * Description:
 *
 * Create by X at 2021/11/12 16:57.
 */
class LogN : ILog {
    override val enable: Boolean
        get() = false

    override fun findTag(ignoreDisable: Boolean) = ""

    override fun println(message: String?, isError: Boolean) {}

    override fun println(messageProvider: () -> String?, isError: Boolean) {}

    override fun v(tag: String?, message: String?, tr: Throwable?) {}

    override fun v(tag: String?, messageProvider: () -> String?, th: Throwable?) {}

    override fun d(tag: String?, message: String?, tr: Throwable?) {}

    override fun d(tag: String?, messageProvider: () -> String?, th: Throwable?) {}

    override fun i(tag: String?, message: String?, tr: Throwable?) {}

    override fun i(tag: String?, messageProvider: () -> String?, th: Throwable?) {}

    override fun w(tag: String?, message: String?, tr: Throwable?) {}

    override fun w(tag: String?, messageProvider: () -> String?, th: Throwable?) {}

    override fun e(tag: String?, message: String?, tr: Throwable?) {}

    override fun e(tag: String?, messageProvider: () -> String?, th: Throwable?) {}

}