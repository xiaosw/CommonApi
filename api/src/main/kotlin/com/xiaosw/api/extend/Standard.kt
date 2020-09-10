package com.xiaosw.api.extend

import com.xiaosw.api.exception.TryCatchException
import com.xiaosw.api.logger.Logger
import com.xiaosw.api.logger.report.ReportManager
import com.xiaosw.api.util.EnvironmentUtil
import java.io.File
import kotlin.jvm.internal.Intrinsics


/**
 * @Description
 *
 * @Date 2019-04-26.
 * @Author xiaosw<xiaosw0802@163.com>.
 */


/**
 * 安全执行代码块，即使代码异常也不抛错。
 */
@JvmOverloads
inline fun <T, R> T.tryCatch(
    errorMessage: String = ""
    , def: R? = null
    , block: (T) -> R
) : R? {
    try {
        return block(this)
    } catch (e: Throwable) {
        Logger.e("tryCatch: errorMessage = $errorMessage", throwable = e)
    }
    return def
}

/**
 * 安全执行代码块，即使代码异常也不抛错，并且上报服务器。
 */
@JvmOverloads
inline fun <T, R> T.tryCatchAndReport(
    errorMessage: String = ""
    , def: R? = null
    , block: (T) -> R
) : R? {
    try {
        return block(this)
    } catch (e: Throwable) {
        Logger.e("tryCatch: errorMessage = $errorMessage", throwable = e)
        ReportManager.reportThrowable(
            TryCatchException(
                errorMessage,
                e
            )
        )
    }
    return def
}

@JvmOverloads
inline fun Any?.isNull() = null == this

@JvmOverloads
inline fun Any?.areEqual(second: Any?, ignoreCase: Boolean = false) : Boolean {
    if (this is String && second is String) {
        return equals(second, ignoreCase)
    }
    return Intrinsics.areEqual(this, second)
}

@JvmOverloads
inline fun Collection<*>?.isNull(onlyNull: Boolean = false) : Boolean {
    this?.let {
        if (onlyNull) {
            return false
        }
        return it.isEmpty()
    }
    return true
}

@JvmOverloads
inline fun Map<*, *>?.isNull(onlyNull: Boolean = false) : Boolean {
    this?.let {
        if (onlyNull) {
            return false
        }
        return it.isEmpty()
    }
    return true
}

@JvmOverloads
inline fun String?.isNull(useTrim: Boolean = false, ignoreNull: Boolean = true) : Boolean {
    this?.let {
        var arg = it
        if (useTrim) {
            arg = it.trim()
        }
        var isEmpty = arg.isEmpty()
        if (isEmpty) {
            return true
        }
        if (!ignoreNull) {
            return false
        }
        arg.equals("null", true)
    }
    return true
}

inline fun File?.delete() {
    this?.let {
        if (isFile) {
            EnvironmentUtil.deleteFile(this)
        } else {
            EnvironmentUtil.deleteDir(this)
        }
    }
}