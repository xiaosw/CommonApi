package com.doudou.log.internal

import com.doudou.log.LogConfig
import com.doudou.log.Logger

/**
 * ClassName: [LogD]
 * Description:
 *
 * Create by X at 2021/11/12 17:41.
 */
internal open class LogD(config: LogConfig) : LogV(config) {

    override val level: Int
        get() = Logger.DEBUG

    override fun println(message: String?, isError: Boolean) {}

    override fun println(messageProvider: () -> String?, isError: Boolean) {}

    override fun v(tag: String?, message: String?, tr: Throwable?) {}

    override fun v(tag: String?, messageProvider: () -> String?, th: Throwable?) {}

}