package com.xiaosw.api.logger

import android.util.Log
import com.xiaosw.api.config.AppConfig

/**
 * @ClassName [Logger]
 * @Description
 *
 * @Date 2019-08-09.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
object Logger {

    /**
     * log single maximum output length.
     */
    private const val MAX_MESSAGE_LEN = 3000

    /** 日志 tag 格式 */
    const val TAG_FORMAT = "%s.%s:L%d"
    val LOG_UTILS_CLASS by lazy {
        Logger::class.java.name
    }

    private val sMessage by lazy {
        mutableListOf<String>()
    }

    var mPreTag = ""
        private set
    private var mLogLevel = LogLevel.DEBUG

    fun init(logLevel: LogLevel, preTag: String = "xiaosw-") {
        mLogLevel = logLevel
        mPreTag = preTag
    }

    @JvmStatic
    inline fun findTag() : String {
        var lastClassIsLogUtils= false
        Thread.currentThread().stackTrace.forEach {
            with(it.className == LOG_UTILS_CLASS) {
                if (!this && lastClassIsLogUtils) { // Target Class
                    val className = it.className
                    val simpleName = className.substring(className.lastIndexOf(".") + 1)
                    return TAG_FORMAT.format("$mPreTag$simpleName", it.methodName, it.lineNumber)
                }
                lastClassIsLogUtils = this
            }
        }
        return mPreTag
    }

    private inline fun splitMessageIfNeeded(message: String) : MutableList<String> {
        sMessage.clear()
        var msg = message
        while (msg.length > MAX_MESSAGE_LEN) {
            sMessage.add(msg.substring(0,
                MAX_MESSAGE_LEN
            ))
            msg = msg.substring(MAX_MESSAGE_LEN)
        }
        if (msg.isNotEmpty()) {
            sMessage.add(msg)
        }
        return sMessage
    }

    @JvmStatic
    fun isEnable() = mLogLevel != LogLevel.NONE

    @JvmStatic
    @JvmOverloads
    fun println(message: String, isError: Boolean = false) {
        if (AppConfig.isDebug && mLogLevel.value <= Log.VERBOSE) {
            splitMessageIfNeeded(message).forEach {
                if (isError) {
                    System.err.println(it)
                } else {
                    println(it)
                }
            }
        }
    }

    @JvmStatic
    @JvmOverloads
    fun v(message: String, tag: String = findTag(), throwable: Throwable? = null) {
        if (AppConfig.isDebug && mLogLevel.value <= Log.VERBOSE) {
            splitMessageIfNeeded(message).forEach {
                throwable?.run {
                    Log.v(tag, it, this)
                } ?: Log.v(tag, it)
            }
        }
    }

    @JvmStatic
    @JvmOverloads
    fun d(message: String, tag: String = findTag(), throwable: Throwable? = null) {
        if (AppConfig.isDebug && mLogLevel.value <= Log.DEBUG) {
            splitMessageIfNeeded(message).forEach {
                throwable?.run {
                    Log.d(tag, it, this)
                } ?: Log.d(tag, it)
            }
        }
    }

    @JvmStatic
    @JvmOverloads
    fun i(message: String, tag: String = findTag(), throwable: Throwable? = null) {
        if (AppConfig.isDebug && mLogLevel.value <= Log.INFO) {
            splitMessageIfNeeded(message).forEach {
                throwable?.run {
                    Log.i(tag, it, this)
                } ?: Log.i(tag, it)
            }
        }
    }

    @JvmStatic
    @JvmOverloads
    fun w(message: String, tag: String = findTag(), throwable: Throwable? = null) {
        if (AppConfig.isDebug && mLogLevel.value <= Log.WARN) {
            splitMessageIfNeeded(message).forEach {
                throwable?.run {
                    Log.w(tag, it, this)
                } ?: Log.w(tag, it)
            }
        }
    }

    @JvmStatic
    @JvmOverloads
    fun e(message: String, tag: String = findTag(), throwable: Throwable? = null) {
        if (AppConfig.isDebug && mLogLevel.value <= Log.ERROR) {
            splitMessageIfNeeded(message).forEach {
                throwable?.run {
                    Log.e(tag, it, this)
                } ?: Log.e(tag, it)
            }
        }
    }

    enum class LogLevel(val value: Int) {
        NONE(Int.MAX_VALUE),
        VERBOSE(Log.VERBOSE),
        DEBUG(Log.DEBUG),
        INFO(Log.INFO),
        WARN(Log.WARN),
        ERROR(Log.ERROR)
    }

}