package com.doudou.log.record

import java.io.File

/**
 * ClassName: [ILogRecordOpen]
 * Description:
 *
 * Create by X at 2022/01/21 18:24.
 */
interface ILogRecordOpen {

    fun getLogFiles() : List<File>?

    fun getLogOutDir() : String?

}