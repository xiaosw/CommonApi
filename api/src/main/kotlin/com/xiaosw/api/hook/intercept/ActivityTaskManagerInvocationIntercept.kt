package com.xiaosw.api.hook.intercept

import com.xiaosw.api.hook.invocation.InvocationHandlerIntercept

/**
 * @ClassName: [ActivityTaskManagerInvocationIntercept]
 * @Description:
 *
 * Created by admin at 2021-01-07
 * @Email xiaosw0802@163.com
 */
class ActivityTaskManagerInvocationIntercept : InvocationHandlerIntercept {

    override fun interceptInvoke(proxy: Any?, methodName: String, args: Array<Any?>) {
        if ("startActivity" == methodName) {
            ActivityNotRegisterInvocationIntercept.replace2ProxyIntent(args)
        }
    }

}