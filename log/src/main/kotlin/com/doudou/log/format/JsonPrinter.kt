package com.doudou.log.format

import android.text.TextUtils
import android.util.Log
import com.doudou.log.LogFormat
import org.json.JSONArray
import org.json.JSONObject


/**
 * ClassName: [JsonPrinter]
 * Description:
 *
 * Create by X at 2021/11/23 15:35.
 */
internal open class JsonPrinter(format: LogFormat) : FormatPrinter(format) {

    override fun println(
        priority: Int,
        tag: String,
        size: Int,
        position: Int,
        message: String,
        threadName: String,
        isException: Boolean
    ) {
        if (!format.formatJson || !isJson(message)) {
            super.println(priority, tag, size, position, message, threadName, isException)
            return
        }
        val formatMsg = " ${format.firstFormatLineHeader}${format.dividerLine}" +
                "${format.newLine}${format.formatLineHeader}Thread:$threadName" +
                "${format.newLine}${format.formatLineHeader}${format.dividerLine}" +
                "${format.newLine}${format.formatLineHeader}$message" +
                "${format.newLine}${format.formatLineHeader}${format.dividerLine}" +
                "${format.newLine}${format.formatLineHeader}${formatJsonStr(message)}" +
                "${format.lastFormatLineHeader}${format.dividerLine}${format.newLine}${format.newLine} "
        Log.println(priority, tag, formatMsg)

    }

    private fun getLevelStr(level: Int): String {
        val levelStr = StringBuffer()
        for (i in 0 until level) {
            levelStr.append("    ")
        }
        return levelStr.toString()
    }

    private fun formatJsonStr(jsonData: String): String {
        var jsonStr = jsonData
        if (!isJson(jsonStr)) {
            return jsonStr
        }
        jsonStr = jsonStr.trim { it <= ' ' }
        var level = 0
        var lastChar = ' '
        val jsonFormatStr = StringBuffer()
        val len = jsonStr.length
        for (i in 0 until len) {
            val c = jsonStr[i]
            if (level > 0 && '\n' == jsonFormatStr[jsonFormatStr.length - 1]) {
                jsonFormatStr.append(getLevelStr(level))
            }
            when (c) {
                '{', '[' -> {
                    if (lastChar != ',') {
                        jsonFormatStr.append(getLevelStr(level))
                    }
                    jsonFormatStr.append(c).append("${format.newLine}${format.formatLineHeader}")
                    level++
                }

                ',' -> jsonFormatStr.append(c)
                    .append("${format.newLine}${format.formatLineHeader}")
                    .append(getLevelStr(level))

                '}', ']' -> {
                    level--
                    jsonFormatStr.append("${format.newLine}${format.formatLineHeader}")
                        .append(getLevelStr(level))
                        .append(c)
                }

                else -> {
                    if (lastChar == '[' || lastChar == '{') {
                        jsonFormatStr.append(getLevelStr(level))
                    }
                    jsonFormatStr.append(c)
                }
            }
            lastChar = c
        }
        return jsonFormatStr.toString()
    }

    /**
     * 判断字符串是否为 json 格式
     */
    companion object {

        fun isJson(json: String): Boolean {
            var json = json
            if (TextUtils.isEmpty(json)) {
                return false
            }
            json = json.trim { it <= ' ' }
            try {
                if (json.startsWith("[")) {
                    JSONArray(json)
                    return true
                } else if (json.startsWith("{")) {
                    JSONObject(json)
                    return true
                }
            } catch (e: Exception) {
                // not need to do anything.
            }

            return false
        }
    }

}