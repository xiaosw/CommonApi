package com.doudou.log.format

import android.util.Log
import com.doudou.log.LogFormat


/**
 * ClassName: [FormatPrinter]
 * Description:
 *
 * Create by X at 2021/11/23 15:36.
 */
internal open class FormatPrinter(val format: LogFormat) : Printer {

    override fun println(
        priority: Int,
        tag: String,
        size: Int,
        position: Int,
        message: String,
        threadName: String,
        isException: Boolean
    ) {
        val formatMsg =  if (isException) {
            "${format.newLine}${format.formatLineHeader}${message.replace(format.newLine, "${format.newLine}${format.formatLineHeader}")}"
        } else {
            "${format.newLine}${format.formatLineHeader}$message"
        }
        // Log.e("ddd", "println: $size, $position, $msg, format = $formatMsg")
        if (position === 0 && size === 1) {
            Log.println(priority, tag, " ${format.firstFormatLineHeader}${format.dividerLine}" +
                    "${format.newLine}${format.formatLineHeader}Thread: $threadName" +
                    "${format.newLine}${format.formatLineHeader}${format.dividerLine}" +
                    "$formatMsg" +
                    "${format.lastFormatLineHeader}${format.dividerLine}${format.newLine}${format.newLine} ")
        } else if (position === 0 && size > 1) {
            Log.println(priority, tag, " ${format.firstFormatLineHeader}${format.dividerLine}" +
                    "${format.newLine}${format.formatLineHeader}Thread: $threadName" +
                    "${format.newLine}${format.formatLineHeader}${format.dividerLine}" +
                    "$formatMsg")
        } else if (position === size - 1) {
            Log.println(priority, "", " $formatMsg" +
                    "${format.lastFormatLineHeader}${format.dividerLine}${format.newLine}${format.newLine} ")
        } else {
            Log.println(priority, "", " $formatMsg")
        }
    }


}