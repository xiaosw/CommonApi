package com.doudou.log.internal

import com.doudou.log.Logger

/**
 * ClassName: [LogW]
 * Description:
 *
 * Create by X at 2021/11/12 17:41.
 */
internal open class LogW(preTag: String? = null) : LogI(preTag) {

    override val level: Int
        get() = Logger.WARN

    override fun println(message: String?, isError: Boolean) {
    }

    override fun v(tag: String?, message: String?, tr: Throwable?) {
    }

    override fun d(tag: String?, message: String?, tr: Throwable?) {
    }

    override fun i(tag: String?, message: String?, tr: Throwable?) {
    }

}