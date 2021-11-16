package com.doudou.log.internal

import com.doudou.log.Logger

/**
 * ClassName: [LogE]
 * Description:
 *
 * Create by X at 2021/11/12 17:41.
 */
internal open class LogE(preTag: String? = null) : LogW(preTag) {

    override val level: Int
        get() = Logger.ERROR

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

}