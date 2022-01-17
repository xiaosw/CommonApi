package com.xiaosw.api.restart

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Process
import com.xiaosw.api.AndroidContext
import com.xiaosw.api.extend.findActivity

/**
 * ClassName: [RestartAppManager]
 * Description:
 *
 * Create by X at 2022/01/17 17:14.
 */
object RestartAppManager {

    @JvmStatic
    @JvmOverloads
    fun restartApp(context: Context = AndroidContext.get()) {
        val ctx = context.findActivity() ?: context
        with(ctx) {
            val isActivity = (this is Activity)
            startActivity(Intent(context, RestartBridgeActivity::class.java).also {
                if (!isActivity) {
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            })
            if (this is Activity) {
                overridePendingTransition(0, 0)
                finish()
            }
        }
        Process.killProcess(Process.myPid())
    }

}