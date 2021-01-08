package com.xiaosw.simple

import android.content.*
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xiaosw.api.annotation.AutoAdjustDensity
import com.xiaosw.api.logger.Logger
import com.xiaosw.api.manager.ActivityLifeManager
import com.xiaosw.api.manager.DensityManager
import com.xiaosw.api.util.FpsMonitor
import com.xsw.compat.start.StartManager
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @ClassName: [MainActivity]
 * @Description:
 *
 * Created by admin at 2019-09-27 16:42
 * @Email xiaosw0802@163.com
 */
@AutoAdjustDensity(baseDp = 360f, baseDpByWidth = true)
class MainActivity : AppCompatActivity(), ActivityLifeManager.AppLifecycleListener {

    private val mFpsCallback by lazy {
        object : FpsMonitor.OnFpsMonitorListener {
            override fun onFpsMonitor(fps: Int) {
//                Logger.i("fps: $fps")
                tv_fps.text = "FPS:$fps"
            }
        }
    }

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

        FpsMonitor.start(mFpsCallback)

        tv_text.setOnClickListener {
            startActivity(Intent(this, NotRegisterActivity::class.java))
        }
    }

    override fun onAppForeground(isFirstLauncher: Boolean) {
        Logger.i("onAppForeground: $isFirstLauncher")
    }

    override fun onAppBackground(activeTime: Long) {
        Logger.i("onAppBackground：activeTime = $activeTime")
    }

}