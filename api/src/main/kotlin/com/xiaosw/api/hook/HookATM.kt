package com.xiaosw.api.hook

import android.content.Context
import com.xiaosw.api.extend.tryCatch

/**
 * @ClassName: [HookATM]
 * @Description:
 *
 * Created by admin at 2021-01-07
 * @Email xiaosw0802@163.com
 */
internal object HookATM : BaseHook() {

    override fun internalHook(context: Context): Boolean {
        return internalHookATM()
    }

    private inline fun internalHookATM() : Boolean {
        // IActivityTaskManagerSingleton
        var target = HookUtil.getDeclaredField(
            HookUtil.safe2Class("android.app.ActivityTaskManager")
            , "IActivityTaskManagerSingleton")?.get(null)
        return target?.run {
            proxyAms(this)
        } ?: false
    }

    private inline fun proxyAms(obj: Any) = obj.tryCatch {
        // Singleton Field
        val mInstanceField = HookUtil.safe2Class("android.util.Singleton")
            ?.getDeclaredField("mInstance")?.also {
                it.isAccessible = true
            }
            ?: return false

        // IActivityTaskManager
        val iActivityTaskManager = mInstanceField?.get(obj) ?: return false

        return proxy(mInstanceField, obj, iActivityTaskManager)
    } ?: false

}