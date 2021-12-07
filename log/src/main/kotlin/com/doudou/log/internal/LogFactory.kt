package com.doudou.log.internal

import com.doudou.log.LogConfig
import com.doudou.log.Logger

/**
 * ClassName: [LogFactory]
 * Description:
 *
 * Create by X at 2021/11/12 17:20.
 */
internal object LogFactory {

    fun create(config: LogConfig) = when(config.level) {
        Logger.VERBOSE -> LogV(config)
        Logger.DEBUG -> LogD(config)
        Logger.INFO -> LogI(config)
        Logger.WARN -> LogW(config)
        Logger.ERROR -> LogE(config)
        else -> LogN()
    }

}