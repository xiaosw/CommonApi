package com.doudou.log.internal

import android.util.Log
import com.doudou.log.Logger

/**
 * ClassName: [LogV]
 * Description:
 *
 * Create by X at 2021/11/12 17:41.
 */
internal open class LogV(preTag: String? = null) : ILog {

    private val internalPreTag = preTag ?: ""

    override val level: Int
        get() = Logger.VERBOSE

    override val enable: Boolean
        get() = true

    override fun println(message: String?, isError: Boolean) {
        splitMessageIfNeeded(message) { _, _, msg ->
            if (isError) {
                System.err.println(msg)
            } else {
                kotlin.io.println(msg)
            }
        }
    }

    override fun v(tag: String?, message: String?, tr: Throwable?) {
        println(Log.VERBOSE, tag, message, tr)
    }

    override fun d(tag: String?, message: String?, tr: Throwable?) {
        println(Log.DEBUG, tag, message, tr)
    }

    override fun i(tag: String?, message: String?, tr: Throwable?) {
        println(Log.INFO, tag, message, tr)
    }

    override fun w(tag: String?, message: String?, tr: Throwable?) {
        println(Log.WARN, tag, message, tr)
    }

    override fun e(tag: String?, message: String?, tr: Throwable?) {
        println(Log.ERROR, tag, message, tr)
    }

    override fun findTag(ignoreDisalbe: Boolean) : String {
        if (!ignoreDisalbe && !enable) {
            return ""
        }
        with(Thread.currentThread().stackTrace) {
            var traceOffset = -1
            var lastClassIsLogUtils= false
            for (position in 0 until size) {
                val e: StackTraceElement = get(position)
                val isLogUtils = e.className == LOG_CLASS
                if (!isLogUtils && lastClassIsLogUtils) { // Target Class
                    traceOffset = position
                    break
                }
                lastClassIsLogUtils = isLogUtils
            }
            if (traceOffset === -1) {
                return internalPreTag
            }
            with(get(traceOffset)) {
                var stackTrace: String = toString()
                stackTrace = stackTrace.substring(stackTrace.lastIndexOf(40.toChar()), stackTrace.length)
                var tag = "%s.%s%s"
                var callerClazzName: String = className
                callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1)
                return String.format(tag, "$internalPreTag$callerClazzName", methodName, stackTrace)
            }
        }
        return internalPreTag
    }

    private fun splitMessageIfNeeded(message: String?, block: (count: Int, position: Int, message: String) -> Unit) {
        message?.let {
            if (it.length <= MAX_MESSAGE_LEN) {
                block(1, 0, it)
                return
            }
            val len = it.length
            val m = len % MAX_MESSAGE_LEN
            val c = len / MAX_MESSAGE_LEN
            val count = if (m === 0) c else c + 1
            var pos = 0
            for (startIndex in 0..len step MAX_MESSAGE_LEN) {
                val endIndex = len.coerceAtMost(startIndex + MAX_MESSAGE_LEN)
                block(count, pos, it.substring(startIndex, endIndex))
                pos += 1
            }
        }
    }

    private fun println(priority: Int, tag: String? = null, msg: String? = "", tr: Throwable? = null) {
        val printTag = if (tag.isNullOrEmpty()) findTag() else tag
        val threadName = Thread.currentThread().name
        LogThreadManager.execute {
            var printMsg = msg ?: EMPTY_STR
            val stackTrace = Log.getStackTraceString(tr)
            if (!stackTrace.isNullOrEmpty()) {
                printMsg = if (printMsg.isNullOrEmpty()) {
                    stackTrace
                } else {
                    "$printMsg\n$stackTrace"
                }
            }
            splitMessageIfNeeded(printMsg) { size, position, msg ->
                try {
                    val formatMsg =  if (printMsg.startsWith("java.lang.")) {
                        "$NEW_LINE$LINE_HEADER${msg.replace(NEW_LINE, "$NEW_LINE$LINE_HEADER")}"
                    } else {
                        "$NEW_LINE$LINE_HEADER$msg"
                    }
                    // Log.e("ddd", "println: $size, $position, $msg, format = $formatMsg")
                    if (position === 0 && size === 1) {
                        Log.println(priority, printTag, " $LINE_HEADER_FIRST$LINE_BORDER" +
                                "$formatMsg" +
                                "$NEW_LINE$LINE_HEADER$LINE_BORDER" +
                                "$NEW_LINE${LINE_HEADER}Thread: $threadName" +
                                "$LINE_HEADER_LAST$LINE_BORDER$NEW_LINE$NEW_LINE ")
                    } else if (position === 0 && size > 1) {
                        Log.println(priority, printTag, " $LINE_HEADER_FIRST$LINE_BORDER$formatMsg")
                    } else if (position === size - 1) {
                        Log.println(priority, "", " $formatMsg" +
                                "$NEW_LINE$LINE_HEADER$LINE_BORDER" +
                                "$NEW_LINE${LINE_HEADER}Thread: $threadName" +
                                "$LINE_HEADER_LAST$LINE_BORDER$NEW_LINE$NEW_LINE ")
                    } else {
                        Log.println(priority, "", " $formatMsg")
                    }
                } catch (ignore: Throwable){}
            }
        }
    }

    companion object {
        const val EMPTY_STR = ""
        /**
         * log single maximum output length.
         */
        private const val MAX_MESSAGE_LEN = 3800

        private val LOG_CLASS: String by lazy {
            Logger::class.java.name
        }

        const val NEW_LINE = "\n"
        const val LINE_HEADER_FIRST = "$NEW_LINE╔"
        const val LINE_HEADER = "║"
        const val LINE_HEADER_LAST = "$NEW_LINE╚"

        const val LINE_BORDER = "═════════════════════════" +
                "══════════════════════════════════════════" +
                "══════════════════════════════════════════"

    }

}