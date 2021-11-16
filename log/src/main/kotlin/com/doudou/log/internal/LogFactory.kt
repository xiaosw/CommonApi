package com.doudou.log.internal

import com.doudou.log.Logger
import com.doudou.log.annotation.Level

/**
 * ClassName: [LogFactory]
 * Description:
 *
 * Create by X at 2021/11/12 17:20.
 */
internal object LogFactory {

    fun create(@Level level: Int, preTag: String? = null) = when(level) {
        Logger.VERBOSE -> LogV(preTag)
        Logger.DEBUG -> LogD(preTag)
        Logger.INFO -> LogI(preTag)
        Logger.WARN -> LogW(preTag)
        Logger.ERROR -> LogE(preTag)
        else -> LogN()
    }

}