package com.doudou.log.internal

import android.util.Log
import com.doudou.log.Logger
import java.lang.StringBuilder

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
        splitMessageIfNeeded(message) {
            if (isError) {
                System.err.println(message)
            } else {
                kotlin.io.println(message)
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

    private fun splitMessageIfNeeded(message: String?, block: (message: String) -> Unit) {
        message?.let {
            if (it.length <= MAX_MESSAGE_LEN) {
                block(it)
                return
            }
            val bytes = it.toByteArray()
            val size = bytes.size
            for (offset in bytes.indices step MAX_MESSAGE_LEN) {
                val length = MAX_MESSAGE_LEN.coerceAtMost(size - offset)
                block(String(bytes, offset, length))
            }
        }
    }

    private fun println(priority: Int, tag: String? = null, msg: String? = "", tr: Throwable? = null) {
        val printTag = if (tag.isNullOrEmpty()) findTag() else tag
        var printMsg = msg ?: EMPTY_STR
        val stackTrace = Log.getStackTraceString(tr)
        if (!stackTrace.isNullOrEmpty()) {
            printMsg = if (printMsg.isNullOrEmpty()) {
                stackTrace
            } else {
                "$printMsg\n$stackTrace"
            }
        }
        val sb = StringBuilder()
        splitMessageIfNeeded(printMsg) {
            sb.append("║$it")
        }
        try {
            Log.println(priority, printTag, " \n╔$LINE_BORDER" +
                    "\n${sb.toString().replace("\n", "\n║")}" +
                    "\n╚$LINE_BORDER\n\n ")
        } catch (ignore: Throwable){}
    }

    companion object {
        const val EMPTY_STR = ""
        /**
         * log single maximum output length.
         */
        private const val MAX_MESSAGE_LEN = 3000

        private val LOG_CLASS: String by lazy {
            Logger::class.java.name
        }

        const val LINE_BORDER = "═════════════════════════" +
                "══════════════════════════════════════════" +
                "══════════════════════════════════════════"
    }

}