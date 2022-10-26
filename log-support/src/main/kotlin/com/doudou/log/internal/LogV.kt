package com.doudou.log.internal

import android.util.Log
import com.doudou.log.LogConfig
import com.doudou.log.Logger
import com.doudou.log.api.ILog
import com.doudou.log.format.JsonPrinter
import com.doudou.log.format.PrinterFactory
import com.doudou.log.record.LogRecordManager

/**
 * ClassName: [LogV]
 * Description:
 *
 * Create by X at 2021/11/12 17:41.
 */
internal open class LogV(val config: LogConfig) : ILog {

    private val mInternalPreTag = config.preTag ?: ""
    private val mMaxLen = config.messageMaxLen
    private val isOnlyRecord = config.behavior.behavior === Logger.BEHAVIOR_ONLY_RECORD
    private val tagMaxLen = config.tagMaxLen
    private val mLoggerClassMap by lazy {
        mutableMapOf<String, Any?>().also {
            it[LOG_CLASS] = LOG_CLASS
            val logClassKt = "$LOG_CLASS$KT"
            it[logClassKt] = logClassKt
            config?.loggerWrapperClassList?.forEach { logClass ->
                logClass?.name?.let { javaClass ->
                    it[javaClass] = javaClass
                    if (!javaClass.endsWith(KT)) {
                        val javaClassKt = "$javaClass$KT"
                        it[javaClassKt] = javaClassKt
                    }
                }
            }
        }
    }

    override val enable: Boolean
        get() = true

    final override fun println(message: String?, isError: Boolean) {
        val tag = findTag()
        var isRecordLogThread = isRecordLogThread(Thread.currentThread())
        LogThreadManager.startLog {
            val priority = if (isError) Log.ERROR else Log.VERBOSE
            splitMessageIfNeeded(message) { _, _, msg ->
                if (config.behavior.behavior != Logger.BEHAVIOR_ONLY_RECORD) {
                    printlnOnlyWrite(tag, msg, isError)
                }
                if (!isRecordLogThread) {
                    LogRecordManager.onLogRecord(priority, tag, msg)
                }
            }
        }
    }

    final override fun println(messageProvider: () -> String?, isError: Boolean) =
        println(messageProvider.invoke(), isError)

    open fun printlnOnlyWrite(tag: String, message: String?, isError: Boolean) {
        if (isError) {
            System.err.println("$tag：$message")
        } else {
            kotlin.io.println("$tag：$message")
        }
    }

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
        try {
            with(Thread.currentThread().stackTrace) {
                var traceOffset = -1
                var lastClassIsLogUtils= false
                for (position in 0 until size) {
                    val e: StackTraceElement = get(position)
//                val isLogClass = mLoggerClassMap.containsKey(e.className)
                    val className = e.className
                    var isLogClass = false
                    for (key in mLoggerClassMap.keys) {
                        if (className.startsWith(key)) {
                            isLogClass = true
                            break
                        }
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
                    stackTrace = stackTrace.substring(stackTrace.lastIndexOf(40.toChar()))
                    var tag = "%s.%s%s"
                    var callerClazzName: String = className
                    callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1)
                    return String.format(tag, "$mInternalPreTag$callerClazzName", methodName, stackTrace)
                }
            }
        } catch (e: Exception) {
            //
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
        val callThread = Thread.currentThread()
        val threadName = callThread.name
        val isRecordLogThread = isRecordLogThread(callThread)
        LogThreadManager.startLog {
            try {
                var printMsg = msg ?: EMPTY_STR
                val stackTrace = Log.getStackTraceString(tr)
                val isException = !stackTrace.isNullOrEmpty()
                val hasJson = if (isException) {
                    printMsg = if (printMsg.isNullOrEmpty()) {
                        stackTrace
                    } else {
                        "$printMsg\n$stackTrace"
                    }
                    false
                } else {
                    JsonPrinter.hasJson(printMsg)
                }

                splitMessageIfNeeded(printMsg) { size, position, msg ->
                    if (!isRecordLogThread) {
                        LogRecordManager.onLogRecord(priority, printTag, msg)
                    }
                    try {
                        if (!isOnlyRecord) {
                            val realTag = if (printTag.length > tagMaxLen) {
                                printTag.substring(0, tagMaxLen)
                            } else printTag
                            PrinterFactory.create(config.format, hasJson)
                                .println(priority, realTag, size, position, msg, threadName, isException)
                        }
                    } catch (ignore: Throwable){}
                }
            } catch (ignore: Throwable) {}
        }
    }

    private fun isRecordLogThread(thread: Thread) : Boolean {
        if (thread.name.startsWith(LogThreadManager.DEF_LOG_THREAD_PREFIX_NAME)) {
            return true
        }
        thread.stackTrace?.forEach {
            it.className?.let { cn ->
                if (cn.startsWith(LOG_RECORD_MANAGER_CLASS)
                    || cn.startsWith(LOG_RECORD_MANAGER_CLASS_KT)) {
                    return true
                }
            }
        }
        return false
    }

    companion object {
        const val EMPTY_STR = ""
        private const val KT = "Kt"
        private val LOG_CLASS: String by lazy {
            Logger::class.java.name
        }

        private val LOG_RECORD_MANAGER_CLASS by lazy {
            LogRecordManager::class.java.name
        }

        private val LOG_RECORD_MANAGER_CLASS_KT : String by lazy {
            "${LOG_RECORD_MANAGER_CLASS}Kt"
        }

    }

}