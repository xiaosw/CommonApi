package com.xiaosw.simple

import android.app.Application
import com.xiaosw.api.logger.Logger
import com.xiaosw.api.logger.loge
import com.xiaosw.api.manager.UIModeManager
import com.xiaosw.api.storage.DataStorageManager
import com.xsw.track.jvmti.JVMTIManager

/**
 * @ClassName: [App]
 * @Description:
 *
 * Created by admin at 2020-09-11
 * @Email xiaosw0802@163.com
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Logger.init(if (BuildConfig.DEBUG) Logger.LogLevel.VERBOSE else Logger.LogLevel.NONE)
        val isAttachJVMTI = JVMTIManager.attachJVMTI(this, true)
        Logger.e("isAttachJVMTI = $isAttachJVMTI")
        loge("light: ${UIModeManager.isLightMode}, dark: ${UIModeManager.isDarkMode}")
        UIModeManager.register(object : UIModeManager.OnUIModeChangeListener {
            override fun onUIModeChange(light: Boolean) {
                loge("light: $light")
            }
        })
        DataStorageManager.init(AppDataStorage(this))
        DataStorageManager.put("abc", "hello")
        loge(DataStorageManager.getString("abc"))
    }

}