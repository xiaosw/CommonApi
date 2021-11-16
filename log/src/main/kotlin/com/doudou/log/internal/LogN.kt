package com.doudou.log.internal

import com.doudou.log.Logger

/**
 * ClassName: [LogN]
 * Description:
 *
 * Create by X at 2021/11/12 16:57.
 */
internal class LogN : LogE() {

    override val level: Int
        get() = Logger.NONE

    override val enable: Boolean
        get() = false

    override fun println(message: String?, isError: Boolean) {
    }

    override fun v(tag: String?, message: String?, tr: Throwable?) {
    }

    override fun d(tag: String?, message: String?, tr: Throwable?) {
    }

    override fun i(tag: String?, message: String?, tr: Throwable?) {
    }

    override fun w(tag: String?, message: String?, tr: Throwable?) {
    }

    override fun e(tag: String?, message: String?, tr: Throwable?) {
    }

}