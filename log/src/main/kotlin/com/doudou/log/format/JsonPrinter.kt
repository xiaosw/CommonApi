package com.doudou.log.format

import android.util.Log
import com.doudou.log.LogFormat
import org.json.JSONArray
import org.json.JSONObject
import java.lang.StringBuilder


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
        if (!format.formatJson) {
            super.println(priority, tag, size, position, message, threadName, isException)
            return
        }
        var isJson = isJson(message)
        var hasJson = false
        val formatJson = StringBuilder()
        if (isJson) {
            formatJson.append(formatJsonStr(message))
        } else if (hasJson(message)) {
            hasJson = true
            var prefixMsg = ""
            var json = ""
            var suffixMsg = ""
            val objStartIndex = message.indexOf("{")
            val arrStartIndex = message.indexOf("[")
            if (objStartIndex != -1 && arrStartIndex != -1) {
                if (objStartIndex < arrStartIndex) {
                    var objEndIndex = message.lastIndexOf("}")
                    if (objEndIndex != -1) {
                        objEndIndex++
                        prefixMsg = message.substring(0, objStartIndex)
                        json = message.substring(objStartIndex, objEndIndex)
                        suffixMsg = message.substring(objEndIndex)
                    }
                } else {
                    var arrEndIndex = message.lastIndexOf("]")
                    if (arrStartIndex != -1) {
                        arrEndIndex++
                        prefixMsg = message.substring(0, arrStartIndex)
                        json = message.substring(arrStartIndex, arrEndIndex)
                        suffixMsg = message.substring(arrEndIndex)
                    }
                }
            } else if (objStartIndex != -1) {
                var objEndIndex = message.lastIndexOf("}")
                if (objEndIndex != -1) {
                    objEndIndex++
                    prefixMsg = message.substring(0, objStartIndex)
                    json = message.substring(objStartIndex, objEndIndex)
                    suffixMsg = message.substring(objEndIndex)
                }
            } else if (arrStartIndex != -1) {
                var arrEndIndex = message.lastIndexOf("]")
                if (arrStartIndex != -1) {
                    arrEndIndex++
                    prefixMsg = message.substring(0, arrStartIndex)
                    json = message.substring(arrStartIndex, arrEndIndex)
                    suffixMsg = message.substring(arrEndIndex)
                }
            }
            if (prefixMsg.isNotEmpty()) {
                formatJson.append("$prefixMsg${format.newLine}${format.formatLineHeader}")
            }
            formatJson.append(formatJsonStr(json))
            if (suffixMsg.isNotEmpty()) {
                formatJson.append("\n${format.formatLineHeader}${suffixMsg.trim()}")
            }
        }
        if (!isJson && !hasJson) {
            super.println(priority, tag, size, position, message, threadName, isException)
            return
        }
        val formatMsg = " ${format.newLine}${format.firstFormatLineHeader}${format.dividerLine}" +
                "${format.newLine}${format.formatLineHeader}Thread:$threadName" +
                "${format.newLine}${format.formatLineHeader}${format.dividerLine}" +
                "${format.newLine}${format.formatLineHeader}$message" +
                "${format.newLine}${format.formatLineHeader}${format.dividerLine}" +
                "${format.newLine}${format.formatLineHeader}$formatJson" +
                "${format.newLine}${format.lastFormatLineHeader}${format.dividerLine}${format.newLine}${format.newLine} "
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

        internal fun hasJson(message: String?) : Boolean {
            if (message.isNullOrEmpty()) {
                return false
            }
            if (isJson(message)) {
                return true
            }
            var json: String? = null
            val objStartIndex = message.indexOf("{")
            val arrStartIndex = message.indexOf("[")
            if (objStartIndex != -1 && arrStartIndex != -1) {
                if (objStartIndex < arrStartIndex) {
                    val objEndIndex = message.indexOf("}")
                    if (objEndIndex != -1) {
                        json = message.substring(objStartIndex, objEndIndex + 1)
                    }
                } else {
                    val arrEndIndex = message.indexOf("]")
                    if (arrStartIndex != -1) {
                        json = message.substring(arrStartIndex, arrEndIndex + 1)
                    }
                }
            } else if (objStartIndex != -1) {
                val objEndIndex = message.indexOf("}")
                if (objEndIndex != -1) {
                    json = message.substring(objStartIndex, objEndIndex + 1)
                }
            } else if (arrStartIndex != -1) {
                val arrEndIndex = message.indexOf("]")
                if (arrStartIndex != -1) {
                    json = message.substring(arrStartIndex, arrEndIndex + 1)
                }
            }
            return isJson(json)
        }

        fun isJson(json: String?): Boolean {
            if (json.isNullOrBlank()) {
                return false
            }
            var json = json
            json = json.trim { it <= ' ' }
            try {
                if (json.startsWith("[") && json.endsWith("]")) {
                    JSONArray(json)
                    return true
                } else if (json.startsWith("{") && json.endsWith("}")) {
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