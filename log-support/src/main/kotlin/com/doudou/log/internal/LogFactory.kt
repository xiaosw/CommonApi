package com.doudou.log.internal

import android.util.Log
import com.doudou.log.LogConfig
import com.doudou.log.api.ILogFactory
import com.doudou.log.record.LogRecordManager

/**
 * ClassName: [LogFactory]
 * Description:
 *
 * Create by X at 2021/11/12 17:20.
 */
internal class LogFactory : ILogFactory {

     override fun create(config: LogConfig) = when(config.behavior.level) {
        Log.VERBOSE -> LogV(config)
        Log.DEBUG -> LogD(config)
        Log.INFO -> LogI(config)
        Log.WARN -> LogW(config)
        Log.ERROR -> LogE(config)
        else -> LogN()
    }.also {
         LogRecordManager.init(config)
     }

}