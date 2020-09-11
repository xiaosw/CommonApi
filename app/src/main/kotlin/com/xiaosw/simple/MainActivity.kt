package com.xiaosw.simple

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xiaosw.api.annotation.AutoAdjustDensity
import com.xiaosw.api.logger.Logger
import com.xiaosw.api.manager.ActivityLifeManager
import com.xiaosw.api.manager.DensityManager

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
    }

    override fun onAppForeground() {
        Logger.e("onAppForeground")
    }

    override fun onAppBackground(activeTime: Long) {
        Logger.e("onAppBackground：activeTime = $activeTime")
    }

}