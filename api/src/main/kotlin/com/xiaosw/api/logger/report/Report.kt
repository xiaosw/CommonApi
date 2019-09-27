package com.xiaosw.api.logger.report


/**
 * @ClassName [Report]
 * @Description
 *
 * @Date 2019-08-30.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
interface Report {

    /**
     * report message to server
     */
    fun reportMessage(content: String)

    /**
     * report throwable to server
     */
    fun reportThrowable(th: Throwable)

}