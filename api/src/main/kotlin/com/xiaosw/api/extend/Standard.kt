package com.xiaosw.api.extend

import com.xiaosw.api.exception.TryCatchException
import com.xiaosw.api.logger.Logger
import com.xiaosw.api.logger.report.ReportManager


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
fun <T, R> T.tryCatch(errorMessage: String = "", block: (T) -> R) : R? {
    try {
        return block(this)
    } catch (e: Throwable) {
        Logger.e("tryCatch: errorMessage = $errorMessage", throwable = e)
    }
    return null
}

/**
 * 安全执行代码块，即使代码异常也不抛错，并且上报服务器。
 */
@JvmOverloads
fun <T, R> T.tryCatchAndReport(errorMessage: String = "", block: (T) -> R) : R? {
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
    return null
}