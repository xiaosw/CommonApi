package com.doudou.log.internal

import android.util.Log
import com.doudou.log.LogConfig

/**
 * ClassName: [LogFactory]
 * Description:
 *
 * Create by X at 2021/11/12 17:20.
 */
internal object LogFactory {

    fun create(config: LogConfig) = when(config.behavior.level) {
        Log.VERBOSE -> LogV(config)
        Log.DEBUG -> LogD(config)
        Log.INFO -> LogI(config)
        Log.WARN -> LogW(config)
        Log.ERROR -> LogE(config)
        else -> LogN()
    }

}