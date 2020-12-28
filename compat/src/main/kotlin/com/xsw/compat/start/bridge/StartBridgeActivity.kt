package com.xsw.compat.start.bridge

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.xiaosw.api.extend.tryCatch
import com.xiaosw.api.logger.Logger

/**
 * @ClassName: [StartBridgeActivity]
 * @Description:
 *
 * Created by admin at 2020-12-25
 * @Email xiaosw0802@163.com
 */
class StartBridgeActivity : Activity() {

    private var mIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setLockScreenSupport()
        super.onCreate(savedInstanceState)
        setOnePixel()

        window.decorView.setBackgroundColor(Color.RED)
        mIntent = intent
        Logger.e("onCreate", TAG)
    }

    private inline fun setLockScreenSupport() {
        with(window) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                decorView.systemUiVisibility = 1792
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                setShowWhenLocked(true)
            } else {
                addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
            }
            addFlags(4194304)
        }
    }

    private inline fun setOnePixel() {
        with(window) {
            setGravity(Gravity.LEFT or Gravity.TOP)
            val params = attributes
            params.x = 0
            params.y = 0
            params.height = 1
            params.width = 1
            attributes = params
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        mIntent = intent
    }

    override fun onResume() {
        super.onResume()
        Logger.e("onResume: ", TAG)
        handleRealIntent(mIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.e("onDestroy", TAG)
    }

    private inline fun handleRealIntent(intent: Intent?) {
        intent?.tryCatch {
            Logger.e("key = " + it.getStringExtra(EXTRA_KEY_START_ID), TAG)
            it.getParcelableExtra<Intent>(EXTRA_KEY_REAL_INTENT)?.let { realIntent ->
                startActivity(realIntent)
            }
        }
        finish()
    }

    companion object {

        const val EXTRA_KEY_REAL_INTENT = "EXTRA_KEY_REAL_INTENT"
        const val EXTRA_KEY_START_ID = "EXTRA_KEY_START_ID"

        private val TAG = StartBridgeActivity::class.java.simpleName

    }

}