package com.xiaosw.api.hook

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Message
import com.xiaosw.api.extend.tryCatch
import com.xiaosw.api.hook.intercept.ActivityNotRegisterInvocationIntercept

/**
 * @ClassName: [HookActivityThread]
 * @Description:
 *
 * Created by admin at 2021-01-08
 * @Email xiaosw0802@163.com
 */
internal object HookActivityThread : BaseHook() {

    private val LAUNCH_ACTIVITY by lazy {
        val hClazz = HookUtil.safe2Class("android.app.ActivityThread\$H")
            ?: return@lazy -1

        val startActivityWhat = HookUtil
            .getDeclaredField(hClazz, "LAUNCH_ACTIVITY", showException = false)
            ?.get(null) as? Int
            ?: -1
        if (-1 != startActivityWhat) {
            return@lazy startActivityWhat
        }

        return@lazy HookUtil.getDeclaredField(hClazz, "RELAUNCH_ACTIVITY", showException = false)
            ?.get(null)
            ?: -1
    }

    override fun internalHook(context: Context): Boolean {
        return internalHookActivityThreadH(context)
    }

    private fun internalHookActivityThreadH(context: Context) : Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return false
        }
        return tryCatch {
            val activityThreadClazz = HookUtil.safe2Class("android.app.ActivityThread")
                ?: return false

            val activityThread = HookUtil.getDeclaredField(activityThreadClazz, "sCurrentActivityThread")
                ?.get(null)
                ?: return false

            val mH = HookUtil.getDeclaredField(activityThreadClazz, "mH")
                ?.get(activityThread) as? Handler
                ?: return false

            HookUtil.getDeclaredField(Handler::class.java, "mCallback")
                ?.set(mH, ActivityThreadHandlerCallBack())

            true
        } ?: false
    }

    private class ActivityThreadHandlerCallBack : Handler.Callback {

        override fun handleMessage(msg: Message): Boolean {
            if (LAUNCH_ACTIVITY === msg.what) {
                ActivityNotRegisterInvocationIntercept.replace2OriginalIntent(msg.obj)
            }
            return false
        }

    }

}