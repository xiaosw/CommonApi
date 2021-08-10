package com.xiaosw.api.logger

import android.util.Log
import androidx.annotation.Keep
import com.xiaosw.api.extend.isNotNull
import com.xiaosw.api.extend.isNull
import com.xiaosw.api.wrapper.GsonWrapper

/**
 * @ClassName [Logger]
 * @Description
 *
 * @Date 2019-08-09.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
@Keep
object Logger {

    /**
     * log single maximum output length.
     */
    private const val MAX_MESSAGE_LEN = 3000

    private const val MAX_TAG_LEN = 84

    private const val TRANSFORM_TAG_TO_MESSAGE = "~"

    private val LOG_UTILS_CLASS: String by lazy {
        Logger::class.java.name
    }

    var mPreTag = "doudou >> "
        private set
    private var mLogLevel = LogLevel.DEBUG

    @JvmStatic
    @JvmOverloads
    fun init(logLevel: LogLevel, preTag: String = "doudou-") {
        mLogLevel = logLevel
        mPreTag = preTag
    }

    @JvmStatic
    @JvmOverloads
    fun findTag(enable: Boolean = isEnable()) : String {
        if (!enable) {
            return ""
        }
        with(Thread.currentThread().stackTrace) {
            var traceOffset = -1
            var lastClassIsLogUtils= false
            for (position in 0 until size) {
                val e: StackTraceElement = get(position)
                val isLogUtils = e.className == LOG_UTILS_CLASS
                if (!isLogUtils && lastClassIsLogUtils) { // Target Class
                    traceOffset = position
                    break
                }
                lastClassIsLogUtils = isLogUtils
            }
            if (traceOffset === -1) {
                return mPreTag
            }
            with(get(traceOffset)) {
                var stackTrace: String = toString()
                stackTrace = stackTrace.substring(stackTrace.lastIndexOf(40.toChar()), stackTrace.length)
                var tag = "%s.%s%s"
                var callerClazzName: String = className
                callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1)
                return String.format(tag, "$mPreTag$callerClazzName", methodName, stackTrace)
            }
        }
        return mPreTag
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

    @JvmStatic
    fun isEnable() = mLogLevel != LogLevel.NONE

    @JvmStatic
    @JvmOverloads
    fun println(message: String?, isError: Boolean = false) {
        if (mLogLevel.value <= Log.VERBOSE) {
            splitMessageIfNeeded(message) {
                if (isError) {
                    System.err.println(GsonWrapper.formatJsonToLog(it))
                } else {
                    kotlin.io.println(GsonWrapper.formatJsonToLog(it))
                }
            }
        }
    }

    private fun printLog(level: Int, tag: String, message: String?, tr: Throwable?) {
        val hasTr = tr.isNotNull()
        if (message.isNull() && !hasTr) {
            return
        }
        var message = message
        val tag = if (tag.length > MAX_TAG_LEN) {
            message = "$tag: $message"
            TRANSFORM_TAG_TO_MESSAGE
        } else tag

        splitMessageIfNeeded(message) {
            if (hasTr) {
                Log.println(level, tag, GsonWrapper.formatJsonToLog(it) + "\n" + Log.getStackTraceString(tr))
                return@splitMessageIfNeeded
            }
            Log.println(level, tag, GsonWrapper.formatJsonToLog(it))
        }
    }

    @JvmStatic
    @JvmOverloads
    fun v(message: String?, tag: String = findTag(), throwable: Throwable? = null) {
        if (mLogLevel.value <= Log.VERBOSE) {
            printLog(Log.VERBOSE, tag, message, throwable)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun d(message: String?, tag: String = findTag(), throwable: Throwable? = null) {
        if (mLogLevel.value <= Log.DEBUG) {
            printLog(Log.DEBUG, tag, message, throwable)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun i(message: String?, tag: String = findTag(), throwable: Throwable? = null) {
        if (mLogLevel.value <= Log.INFO) {
            printLog(Log.INFO, tag, message, throwable)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun w(message: String?, tag: String = findTag(), throwable: Throwable? = null) {
        if (mLogLevel.value <= Log.WARN) {
            printLog(Log.WARN, tag, message, throwable)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun e(message: String? = "", tag: String = findTag(), throwable: Throwable? = null) {
        message?.let {
            if (mLogLevel.value <= Log.ERROR) {
                printLog(Log.ERROR, tag, message, throwable)
            }
        }
    }

    @JvmStatic
    fun e(throwable: Throwable? = null) {
        if (mLogLevel.value <= Log.ERROR) {
            printLog(Log.ERROR, findTag(), "", throwable)
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

///////////////////////////////////////////////////////////////////////////
// ext
///////////////////////////////////////////////////////////////////////////
inline fun Any.logp(message: String?, isError: Boolean = false) = Logger.println(message, isError)


inline fun Any.logv(message: String?, tag: String = Logger.findTag(), throwable: Throwable? = null) =
    Logger.v(message, tag, throwable)

inline fun Any.logd(message: String?, tag: String = Logger.findTag(), throwable: Throwable? = null) =
    Logger.d(message, tag, throwable)

inline fun Any.logi(message: String?, tag: String = Logger.findTag(), throwable: Throwable? = null) =
    Logger.i(message, tag, throwable)

inline fun Any.logw(message: String?, tag: String = Logger.findTag(), throwable: Throwable? = null) =
    Logger.w(message, tag, throwable)

inline fun Any.loge(message: String? = "", tag: String = Logger.findTag(), throwable: Throwable? = null) =
    Logger.e(message, tag, throwable)

inline fun Any.loge(throwable: Throwable? = null) = Logger.e(throwable)