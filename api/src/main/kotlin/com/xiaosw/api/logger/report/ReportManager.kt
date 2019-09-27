package com.xiaosw.api.logger.report

import com.xiaosw.api.extend.tryCatch

/**
 * @ClassName [ReportManager]
 * @Description
 *
 * @Date 2019-08-30.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
object ReportManager {

    private var report: Report? = null

    fun init(report: Report) {
        ReportManager.report = report
    }

    /**
     * report message to server
     */
    @JvmStatic
    fun reportMessage(content: String) = tryCatch {
        report?.reportMessage(content)
    }

    /**
     * report throwable to server
     */
    @JvmStatic
    fun reportThrowable(th: Throwable) = tryCatch {
        report?.reportThrowable(th)
    }

}