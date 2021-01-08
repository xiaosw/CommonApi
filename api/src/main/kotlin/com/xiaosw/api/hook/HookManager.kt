package com.xiaosw.api.hook

import android.os.Build
import com.xiaosw.api.hook.intercept.ActivityNotRegisterInvocationIntercept
import com.xiaosw.api.hook.intercept.ActivityTaskManagerInvocationIntercept
import com.xiaosw.api.hook.intercept.AMSInvocationIntercept
import com.xiaosw.api.logger.Logger

/**
 * @ClassName: [HookManager]
 * @Description:
 *
 * Created by admin at 2021-01-07
 * @Email xiaosw0802@163.com
 */
object HookManager {

    private val mReceiverInvocationIntercept by lazy {
        AMSInvocationIntercept()
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
        HookActivityThread.hook()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // android.content.ActivityNotFoundException
            HookATM.register(mActivityTaskManagerInvocationIntercept)
        }
        // HookPM.register(mActivityNotRegisterInvocationIntercept)
        return HookPM.isHookSuccess()
    }

}