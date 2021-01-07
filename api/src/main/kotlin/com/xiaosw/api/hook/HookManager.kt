package com.xiaosw.api.hook

import com.xiaosw.api.hook.intercept.ActivityNotRegisterInvocationIntercept
import com.xiaosw.api.hook.intercept.ActivityTaskManagerInvocationIntercept
import com.xiaosw.api.hook.intercept.ReceiverInvocationIntercept

/**
 * @ClassName: [HookManager]
 * @Description:
 *
 * Created by admin at 2021-01-07
 * @Email xiaosw0802@163.com
 */
object HookManager {

    private val mReceiverInvocationIntercept by lazy {
        ReceiverInvocationIntercept()
    }

    private val mActivityNotRegisterInvocationIntercept by lazy {
        ActivityNotRegisterInvocationIntercept()
    }

    private val mActivityTaskManagerInvocationIntercept by lazy {
        ActivityTaskManagerInvocationIntercept()
    }

    fun autoManagerReceiver() : Boolean {
        HookAMS.register(mReceiverInvocationIntercept)
        return HookAMS.isHookSuccess()
    }

    fun enableActivityNotRegister() : Boolean {
         // android.content.ActivityNotFoundException
        HookATM.register(mActivityTaskManagerInvocationIntercept)
        HookPM.register(mActivityNotRegisterInvocationIntercept)
        return HookPM.isHookSuccess()
    }

}