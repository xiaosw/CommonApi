package com.doudou.log.record

/**
 * ClassName: [ILogRecord]
 * Description:
 *
 * Create by X at 2021/12/07 16:01.
 */
interface ILogRecord {

    fun onRecord(priority: Int, tag: String, msg: String)

}