package com.xiaosw.api.restart

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Process
import com.xiaosw.api.AndroidContext
import com.xiaosw.api.extend.showToast
import com.xiaosw.api.util.ToastUtil

/**
 * ClassName: [RestartBridgeActivity]
 * Description:
 *
 * Create by X at 2022/01/17 17:16.
 */
class RestartBridgeActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidContext.init(application)
        handleRestart()
    }

    private fun handleRestart() {
        showToast("重启中...")
        startActivity(packageManager.getLaunchIntentForPackage(packageName))
        overridePendingTransition(0, 0)
        finish()
        Handler(Looper.getMainLooper()).postDelayed({
            Process.killProcess(Process.myPid())
        }, 1_000)
    }

}