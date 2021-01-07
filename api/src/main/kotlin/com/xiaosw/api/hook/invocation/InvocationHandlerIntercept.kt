package com.xiaosw.api.hook.invocation

/**
 * @ClassName: [InvocationHandlerIntercept]
 * @Description:
 *
 * Created by admin at 2021-01-07
 * @Email xiaosw0802@163.com
 */
internal interface InvocationHandlerIntercept {

    fun interceptInvoke(proxy: Any?, methodName: String, args: Array<Any?>)

}