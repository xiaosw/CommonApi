package com.doudou.log.record

import java.io.File

/**
 * ClassName: [LogRecordIgnore]
 * Description:
 *
 * Create by X at 2022/01/21 18:11.
 */
internal class LogRecordIgnore : ILogRecordInternal {

    override fun onLogRecord(priority: Int, tag: String, msg: String) = false

    override fun getLogFiles(): List<File>? = null

    override fun getLogOutDir(): String? = null

}