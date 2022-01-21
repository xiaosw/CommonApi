package com.doudou.log.record

import android.content.Context
import com.doudou.log.LogConfig
import com.doudou.log.Logger
import com.doudou.log.logw


/**
 * ClassName: [LogRecordManager]
 * Description:
 *
 * Create by X at 2022/01/20 09:43.
 */
object LogRecordManager : ILogRecordOpen {

    private var mRecord: ILogRecordInternal = LogRecordIgnore()

    internal fun init(context: Context, config: LogConfig) {
        if (config.behavior == Logger.Behavior.NONE
            || config.behavior.behavior == Logger.BEHAVIOR_ONLY_PRINT) {
            logw("app disable save log to disk.")
            return
        }
        context?.let {
            (it.externalCacheDir ?: it.cacheDir)?.absolutePath?.let { cacheDir ->
                if (cacheDir.isNotEmpty()) {
                    mRecord = LogRecordWrite("$cacheDir/logger/record")
                }
            }
        }
    }

    internal fun onLogRecord(priority: Int, tag: String, msg: String) =
        mRecord.onLogRecord(priority, tag, msg)

    override fun getLogFiles() = mRecord.getLogFiles()

    override fun getLogOutDir() = mRecord.getLogOutDir()
}