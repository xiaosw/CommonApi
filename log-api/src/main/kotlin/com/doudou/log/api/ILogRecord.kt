package com.doudou.log.api

import java.io.File

/**
 * ClassName: [ILogRecord]
 * Description:
 *
 * Create by X at 2022/01/21 18:24.
 */
interface ILogRecord {

    fun getLogFiles() : List<File>?

    fun getLogOutDir() : String?

}