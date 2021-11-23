package com.doudou.log

import android.util.Log
import com.doudou.log.annotation.Level
import com.doudou.log.internal.ILog
import com.doudou.log.internal.LogFactory

/**
 * ClassName: [Logger]
 * Description:
 *
 * Create by X at 2021/11/12 16:56.
 */
object Logger {

    const val VERBOSE = Log.VERBOSE
    const val DEBUG = Log.DEBUG
    const val INFO = Log.INFO
    const val WARN = Log.WARN
    const val ERROR = Log.ERROR
    const val NONE = -1

    private var mLog: ILog = LogFactory.create(LogConfig(NONE))

    val enable = mLog.enable

    @JvmStatic
    fun init(config: LogConfig) {
        mLog = LogFactory.create(config)
    }

    @JvmStatic
    @JvmOverloads
    fun findTag(ignoreDisable: Boolean = false) {
        mLog.findTag(ignoreDisable)
    }

    @JvmStatic
    @JvmOverloads
    fun println(message: String? = null, isError: Boolean = false) {
        mLog.println(message, isError)
    }

    @JvmStatic
    @JvmOverloads
    fun v(message: String? = null, tag: String? = null, tr: Throwable? = null) {
        mLog.v(tag, message, tr)
    }

    @JvmStatic
    @JvmOverloads
    fun d(message: String? = null, tag: String? = null, tr: Throwable? = null) {
        mLog.d(tag, message, tr)
    }

    @JvmStatic
    @JvmOverloads
    fun i(message: String? = null, tag: String? = null, tr: Throwable? = null) {
        mLog.i(tag, message, tr)
    }

    @JvmStatic
    @JvmOverloads
    fun w(message: String? = null, tag: String? = null, tr: Throwable? = null) {
        mLog.w(tag, message, tr)
    }

    @JvmStatic
    @JvmOverloads
    fun e(message: String? = null, tag: String? = null, tr: Throwable? = null) {
        mLog.e(tag, message, tr)
    }

    @JvmStatic
    fun e(tr: Throwable? = null) {
        mLog.e(null, null, tr)
    }

}

///////////////////////////////////////////////////////////////////////////
// ext
///////////////////////////////////////////////////////////////////////////
inline fun logp(message: String? = null, isError: Boolean = false) = Logger.println(message, isError)
inline fun logv(message: String? = null, tag: String? = null, tr: Throwable? = null) = Logger.v(message, tag, tr)
inline fun logd(message: String? = null, tag: String? = null, tr: Throwable? = null) = Logger.d(message, tag, tr)
inline fun logi(message: String? = null, tag: String? = null, tr: Throwable? = null) = Logger.i(message, tag, tr)
inline fun logw(message: String? = null, tag: String? = null, tr: Throwable? = null) = Logger.w(message, tag, tr)
inline fun loge(message: String? = null, tag: String? = null, tr: Throwable? = null) = Logger.e(message, tag, tr)
inline fun loge(tr: Throwable? = null) = Logger.e(null, null, tr = tr)