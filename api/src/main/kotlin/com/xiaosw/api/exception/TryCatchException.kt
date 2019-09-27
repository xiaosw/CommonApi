package com.xiaosw.api.exception

/**
 * @ClassName [TryCatchException]
 * @Description
 *
 * @Date 2019-08-30.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
class TryCatchException : Exception {

    constructor(message: String) : super(message)

    constructor(tr: Throwable) : super(tr)

    constructor(message: String?, tr: Throwable? = null) : super(message, tr)

}