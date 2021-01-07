package com.xiaosw.api.hook

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import com.xiaosw.api.extend.tryCatch

/**
 * @ClassName: [HookAMS]
 * @Description:
 * @Link https://blog.csdn.net/gdutxiaoxu/article/details/81459910
 *
 * Created by admin at 2021-01-07
 * @Email xiaosw0802@163.com
 */
internal object HookAMS : BaseHook() {

    override fun internalHook(context: Context): Boolean {
        return internalHookAMS()
    }

    private inline fun internalHookAMS() : Boolean {
        var target = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // IActivityManagerSingleton
            HookUtil.getDeclaredField(
                ActivityManager::class.java
                , "IActivityManagerSingleton")?.get(null)
        } else {
            HookUtil.safe2Class("android.app.ActivityManagerNative")?.run {
                // ActivityManagerNative
                HookUtil.getDeclaredField(this, "gDefault")?.get(null)
            }
        }
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

        // IActivityManager
        val iActivityManager = mInstanceField?.get(obj) ?: return false

        return proxy(mInstanceField, obj, iActivityManager)
    } ?: false

}