package com.doudou.log.internal

import android.util.Log
import com.doudou.log.LogConfig
import com.doudou.log.Logger
import com.doudou.log.format.JsonPrinter
import com.doudou.log.format.PrinterFactory

/**
 * ClassName: [LogV]
 * Description:
 *
 * Create by X at 2021/11/12 17:41.
 */
internal open class LogV(val config: LogConfig) : ILog {

    private val mInternalPreTag = config.preTag ?: ""
    private val mMaxLen = config.maxLen

    override val level: Int
        get() = Logger.VERBOSE

    override val enable: Boolean
        get() = true

    override fun println(message: String?, isError: Boolean) {
        val tag = findTag()
        var isRecordLog = isRecordLog()
        splitMessageIfNeeded(message) { _, _, msg ->
            val priority = if (isError) {
                System.err.println("$tag：$msg")
                Log.ERROR
            } else {
                kotlin.io.println("$tag：$msg")
                Log.VERBOSE
            }
            if (!isRecordLog) {
                config.record?.onRecord(priority, tag, msg)
            }
        }
    }

    override fun println(messageProvider: () -> String?, isError: Boolean) =
        println(messageProvider.invoke(), isError)

    override fun v(tag: String?, message: String?, tr: Throwable?) {
        println(Log.VERBOSE, tag, message, tr)
    }

    override fun v(tag: String?, messageProvider: () -> String?, th: Throwable?) =
        v(tag, messageProvider.invoke(), th)

    override fun d(tag: String?, message: String?, tr: Throwable?) {
        println(Log.DEBUG, tag, message, tr)
    }

    override fun d(tag: String?, messageProvider: () -> String?, th: Throwable?) =
        d(tag, messageProvider.invoke(), th)

    override fun i(tag: String?, message: String?, tr: Throwable?) {
        println(Log.INFO, tag, message, tr)
    }

    override fun i(tag: String?, messageProvider: () -> String?, th: Throwable?) =
        i(tag, messageProvider.invoke(), th)

    override fun w(tag: String?, message: String?, tr: Throwable?) {
        println(Log.WARN, tag, message, tr)
    }

    override fun w(tag: String?, messageProvider: () -> String?, th: Throwable?) =
        w(tag, messageProvider.invoke(), th)

    override fun e(tag: String?, message: String?, tr: Throwable?) {
        println(Log.ERROR, tag, message, tr)
    }

    override fun e(tag: String?, messageProvider: () -> String?, th: Throwable?) =
        e(tag, messageProvider.invoke(), th)

    override fun findTag(ignoreDisalbe: Boolean) : String {
        if (!ignoreDisalbe && !enable) {
            return ""
        }
        with(Thread.currentThread().stackTrace) {
            var traceOffset = -1
            var lastClassIsLogUtils= false
            for (position in 0 until size) {
                val e: StackTraceElement = get(position)
                val isLogClass = e.className.let {
                    it == LOG_CLASS || it == LOG_CLASS_KT
                }
                if (!isLogClass && lastClassIsLogUtils) { // Target Class
                    traceOffset = position
                    break
                }
                lastClassIsLogUtils = isLogClass
            }
            if (traceOffset === -1) {
                return mInternalPreTag
            }
            with(get(traceOffset)) {
                var stackTrace: String = toString()
                stackTrace = stackTrace.substring(stackTrace.lastIndexOf(40.toChar()), stackTrace.length)
                var tag = "%s.%s%s"
                var callerClazzName: String = className
                callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1)
                return String.format(tag, "$mInternalPreTag$callerClazzName", methodName, stackTrace)
            }
        }
        return mInternalPreTag
    }

    private fun splitMessageIfNeeded(message: String?, block: (count: Int, position: Int, message: String) -> Unit) {
        message?.let {
            if (it.length <= mMaxLen) {
                block(1, 0, it)
                return
            }
            val len = it.length
            val m = len % mMaxLen
            val c = len / mMaxLen
            val count = if (m === 0) c else c + 1
            var pos = 0
            for (startIndex in 0..len step mMaxLen) {
                val endIndex = len.coerceAtMost(startIndex + mMaxLen)
                block(count, pos, it.substring(startIndex, endIndex))
                pos += 1
            }
        }
    }

    private fun println(priority: Int, tag: String? = null, msg: String? = "", tr: Throwable? = null) {
        val printTag = if (tag.isNullOrEmpty()) findTag() else tag
        val threadName = Thread.currentThread().name
        var isRecordLog = isRecordLog()
        LogThreadManager.execute {
            var printMsg = msg ?: EMPTY_STR
            val stackTrace = Log.getStackTraceString(tr)
            val isException = !stackTrace.isNullOrEmpty()
            val isJson = if (isException) {
                printMsg = if (printMsg.isNullOrEmpty()) {
                    stackTrace
                } else {
                    "$printMsg\n$stackTrace"
                }
                false
            } else {
                JsonPrinter.isJson(printMsg)
            }

            splitMessageIfNeeded(printMsg) { size, position, msg ->
                if (!isRecordLog) {
                    config.record?.onRecord(priority, printTag, msg)
                }
                try {
                    PrinterFactory.create(config.format, isJson)
                        .println(priority, printTag, size, position, msg, threadName, isException)
                } catch (ignore: Throwable){}
            }
        }
    }

    private fun isRecordLog() : Boolean {
        Thread.currentThread().stackTrace?.forEach {
            it.className?.let { cn ->
                if (cn.startsWith(LOG_RECORD_MANAGER_CLASS) || cn.startsWith(LOG_RECORD_MANAGER_CLASS_KT)) {
                    return true
                }
            }
        }
        return false
    }

    companion object {
        const val EMPTY_STR = ""
        private val LOG_CLASS: String by lazy {
            Logger::class.java.name
        }

        private val LOG_CLASS_KT: String by lazy {
            "${LOG_CLASS}Kt"
        }

        private const val LOG_RECORD_MANAGER_CLASS = "com.xiaosw.api.manager.LogRecordManager"
        private const val LOG_RECORD_MANAGER_CLASS_KT = "com.xiaosw.api.manager.LogRecordManagerKt"

    }

}