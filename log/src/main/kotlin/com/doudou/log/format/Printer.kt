package com.doudou.log.format

/**
 * ClassName: [Printer]
 * Description:
 *
 * Create by X at 2021/11/23 15:29.
 */
internal interface Printer {

    fun println(
        priority: Int
        , tag: String
        , size: Int
        , position: Int
        , message: String
        , threadName: String
        , isException: Boolean)

}