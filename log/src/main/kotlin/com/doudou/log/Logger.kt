package com.doudou.log

import android.content.Context
import android.util.Log
import com.doudou.log.internal.ILog
import com.doudou.log.internal.LogFactory
import com.doudou.log.record.LogRecordManager

/**
 * ClassName: [Logger]
 * Description:
 *
 * Create by X at 2021/11/12 16:56.
 */
object Logger {

    internal const val NONE = -1
    private const val BEHAVIOR_NONE = NONE
    internal const val BEHAVIOR_ONLY_PRINT = 1
    internal const val BEHAVIOR_ONLY_RECORD = 2
    private const val BEHAVIOR_ALL = 4

    private var mLog: ILog = LogFactory.create(LogConfig(Behavior.NONE))

    val enable = mLog.enable

    @JvmStatic
    fun init(context: Context, config: LogConfig) {
        mLog = LogFactory.create(config)
        LogRecordManager.init(context, config)

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
    fun println(isError: Boolean = false, messageProvider: () -> String?) {
        mLog.println(messageProvider, isError)
    }

    @JvmStatic
    @JvmOverloads
    fun v(message: String? = null, tag: String? = null, tr: Throwable? = null) {
        mLog.v(tag, message, tr)
    }

    @JvmStatic
    @JvmOverloads
    fun v(tag: String? = null, tr: Throwable? = null, messageProvider: () -> String?) {
        mLog.v(tag, messageProvider, tr)
    }

    @JvmStatic
    @JvmOverloads
    fun d(message: String? = null, tag: String? = null, tr: Throwable? = null) {
        mLog.d(tag, message, tr)
    }

    @JvmStatic
    @JvmOverloads
    fun d(tag: String? = null, tr: Throwable? = null, messageProvider: () -> String?) {
        mLog.d(tag, messageProvider, tr)
    }

    @JvmStatic
    @JvmOverloads
    fun i(message: String? = null, tag: String? = null, tr: Throwable? = null) {
        mLog.i(tag, message, tr)
    }

    @JvmStatic
    @JvmOverloads
    fun i(tag: String? = null, tr: Throwable? = null, messageProvider: () -> String?) {
        mLog.i(tag, messageProvider, tr)
    }

    @JvmStatic
    @JvmOverloads
    fun w(message: String? = null, tag: String? = null, tr: Throwable? = null) {
        mLog.w(tag, message, tr)
    }

    @JvmStatic
    @JvmOverloads
    fun w(tag: String? = null, tr: Throwable? = null, messageProvider: () -> String?) {
        mLog.w(tag, messageProvider, tr)
    }

    @JvmStatic
    @JvmOverloads
    fun e(message: String? = null, tag: String? = null, tr: Throwable? = null) {
        mLog.e(tag, message, tr)
    }

    @JvmStatic
    @JvmOverloads
    fun e(tag: String? = null, tr: Throwable? = null, messageProvider: () -> String?) {
        mLog.e(tag, messageProvider, tr)
    }

    @JvmStatic
    fun e(tr: Throwable? = null) {
        mLog.e(null, null, tr)
    }

    enum class Behavior(val level: Int, val behavior: Int) {
        NONE(Logger.NONE, BEHAVIOR_NONE),
        V_ONLY_PRINT(Log.VERBOSE, BEHAVIOR_ONLY_PRINT),
        V_ONLY_RECORD(Log.VERBOSE, BEHAVIOR_ONLY_RECORD),
        V_ALL(Log.VERBOSE, BEHAVIOR_ALL),
        D_ONLY_PRINT(Log.DEBUG, BEHAVIOR_ONLY_PRINT),
        D_ONLY_RECORD(Log.DEBUG, BEHAVIOR_ONLY_RECORD),
        D_ALL(Log.DEBUG, BEHAVIOR_ALL),
        I_ONLY_PRINT(Log.INFO, BEHAVIOR_ONLY_PRINT),
        I_ONLY_RECORD(Log.INFO, BEHAVIOR_ONLY_RECORD),
        I_ALL(Log.INFO, BEHAVIOR_ALL),
        W_ONLY_PRINT(Log.WARN, BEHAVIOR_ONLY_PRINT),
        W_ONLY_RECORD(Log.WARN, BEHAVIOR_ONLY_RECORD),
        W_ALL(Log.WARN, BEHAVIOR_ALL),
        E_ONLY_PRINT(Log.ERROR, BEHAVIOR_ONLY_PRINT),
        E_ONLY_RECORD(Log.ERROR, BEHAVIOR_ONLY_RECORD),
        E_ALL(Log.ERROR, BEHAVIOR_ALL),
    }

}

///////////////////////////////////////////////////////////////////////////
// ext
///////////////////////////////////////////////////////////////////////////
inline fun logp(message: String? = null, isError: Boolean = false) = Logger.println(message, isError)
fun logp(isError: Boolean = false, messageProvider: () -> String?) = Logger.println(isError, messageProvider)
inline fun logv(message: String? = null, tag: String? = null, tr: Throwable? = null) = Logger.v(message, tag, tr)
fun logv(tag: String? = null, tr: Throwable? = null, messageProvider: () -> String?) = Logger.v(tag, tr, messageProvider)
inline fun logd(message: String? = null, tag: String? = null, tr: Throwable? = null) = Logger.d(message, tag, tr)
fun logd(tag: String? = null, tr: Throwable? = null, messageProvider: () -> String?) = Logger.d(tag, tr, messageProvider)
inline fun logi(message: String? = null, tag: String? = null, tr: Throwable? = null) = Logger.i(message, tag, tr)
fun logi(tag: String? = null, tr: Throwable? = null, messageProvider: () -> String?) = Logger.i(tag, tr, messageProvider)
inline fun logw(message: String? = null, tag: String? = null, tr: Throwable? = null) = Logger.w(message, tag, tr)
fun logw(tag: String? = null, tr: Throwable? = null, messageProvider: () -> String?) = Logger.w(tag, tr, messageProvider)
inline fun loge(message: String? = null, tag: String? = null, tr: Throwable? = null) = Logger.e(message, tag, tr)
fun loge(tag: String? = null, tr: Throwable? = null, messageProvider: () -> String?) = Logger.e(tag, tr, messageProvider)
inline fun loge(tr: Throwable? = null) = Logger.e(null, null, tr = tr)