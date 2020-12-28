package com.xiaosw.simple

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.xiaosw.api.annotation.AutoAdjustDensity
import com.xiaosw.api.logger.Logger
import com.xiaosw.api.manager.ActivityLifeManager
import com.xiaosw.api.manager.DensityManager
import com.xsw.compat.start.StartManager

/**
 * @ClassName: [MainActivity]
 * @Description:
 *
 * Created by admin at 2019-09-27 16:42
 * @Email xiaosw0802@163.com
 */
@AutoAdjustDensity(baseDp = 360f, baseDpByWidth = true)
class MainActivity : AppCompatActivity(), ActivityLifeManager.AppLifecycleListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        DensityManager.addThirdAutoAdjustPage(javaClass)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityLifeManager.registerAppLifecycleListener(this)

        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val result = StartManager.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("pinduoduo://")))
                Logger.i("result = $result")
            }

        }, IntentFilter().also {
            it.addAction(Intent.ACTION_SCREEN_OFF)
        })
    }

    override fun onAppForeground() {
        Logger.i("onAppForeground")
    }

    override fun onAppBackground(activeTime: Long) {
        Logger.i("onAppBackground：activeTime = $activeTime")
    }

}