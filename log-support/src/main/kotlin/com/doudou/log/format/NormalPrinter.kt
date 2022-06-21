package com.doudou.log.format

import android.util.Log
import com.doudou.log.LogFormat


/**
 * ClassName: [NormalPrinter]
 * Description:
 *
 * Create by X at 2021/11/23 15:37.
 */
internal class NormalPrinter : JsonPrinter(LogFormat(false)) {

    override fun println(
        priority: Int,
        tag: String,
        size: Int,
        position: Int,
        message: String,
        threadName: String,
        isException: Boolean
    ) {
        Log.println(priority, tag, message)
    }


}