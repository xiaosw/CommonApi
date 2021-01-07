package com.xiaosw.api.hook

import android.content.Context
import com.xiaosw.api.extend.tryCatch

/**
 * @ClassName: [HookPM]
 * @Description:
 *
 * Created by admin at 2021-01-07
 * @Email xiaosw0802@163.com
 */
internal object HookPM : BaseHook() {

    override fun internalHook(context: Context) = tryCatch {
        val activityThreadClazz = HookUtil.safe2Class("android.app.ActivityThread")
            ?: return false

        val activityThread = HookUtil
            .getDeclaredField(activityThreadClazz, "sCurrentActivityThread")
            ?.get(null)
            ?: return false

        val iPackageManager = HookUtil.getDeclaredField(activityThreadClazz, "sPackageManager")
            ?.get(activityThread) ?: return false

        val sPackageManagerField = HookUtil.getDeclaredField(activityThreadClazz, "sPackageManager")
            ?: return false

        return proxy(sPackageManagerField, activityThread, iPackageManager)
    } ?: false

}