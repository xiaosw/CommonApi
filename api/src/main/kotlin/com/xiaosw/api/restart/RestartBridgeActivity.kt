package com.xiaosw.api.restart

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.view.KeyEvent
import com.xiaosw.api.AndroidContext
import com.xiaosw.api.extend.showToast
import com.xiaosw.api.extend.tryCatch
import com.xiaosw.api.util.ToastUtil

/**
 * ClassName: [RestartBridgeActivity]
 * Description:
 *
 * Create by X at 2022/01/17 17:16.
 */
class RestartBridgeActivity : Activity() {

    private var isChangeAppIcon = false
    private var mHomeCodeReceiver: HomeCodeReceiver? = null
    private var isClickHome = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidContext.init(application)
        handleRestart()
    }

    override fun onResume() {
        isClickHome = false
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        if (isChangeAppIcon && !isClickHome) {
            launcherMain()
        }
    }

    override fun onDestroy() {
        mHomeCodeReceiver?.let {
            tryCatch {
                unregisterReceiver(mHomeCodeReceiver)
            }
        }
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (isChangeAppIcon && keyCode === KeyEvent.KEYCODE_BACK) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun handleRestart() {
        isChangeAppIcon = intent.getBooleanExtra(KEY_CHANGE_APP_ICON, false)
        if (isChangeAppIcon) {
            mHomeCodeReceiver = HomeCodeReceiver().also {
                val filter = IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                registerReceiver(mHomeCodeReceiver, filter)
            }
            return
        }
        showToast("重启中...")
        launcherMain()
    }

    private fun launcherMain() {
//        startActivity(packageManager.getLaunchIntentForPackage(packageName))
        startActivity(Intent(this, Class.forName("com.xiaosw.simple.MainAliasActivity")))
        overridePendingTransition(0, 0)
        finish()
        Handler(Looper.getMainLooper()).postDelayed({
            Process.killProcess(Process.myPid())
        }, 1_000)
    }

    inner class HomeCodeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

        }
    }

    companion object {
        const val KEY_CHANGE_APP_ICON = "key_change_app_icon"
    }

}