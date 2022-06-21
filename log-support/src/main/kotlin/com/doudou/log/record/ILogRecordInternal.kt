package com.doudou.log.record

/**
 * ClassName: [ILogRecordInternal]
 * Description:
 *
 * Create by X at 2022/01/21 18:10.
 */
internal interface ILogRecordInternal : ILogRecordOpen {

    fun onLogRecord(priority: Int, tag: String, msg: String) : Boolean

}