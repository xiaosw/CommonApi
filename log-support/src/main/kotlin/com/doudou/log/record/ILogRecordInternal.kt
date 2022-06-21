package com.doudou.log.record

import com.doudou.log.api.ILogRecord

/**
 * ClassName: [ILogRecordInternal]
 * Description:
 *
 * Create by X at 2022/01/21 18:10.
 */
internal interface ILogRecordInternal : ILogRecord {

    fun onLogRecord(priority: Int, tag: String, msg: String) : Boolean

}