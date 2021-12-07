package com.xiaosw.simple

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.doudou.keep.KeepAliveManager
import com.doudou.log.Logger
import com.doudou.log.loge
import com.xiaosw.api.extend.processName
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
class App : MultiDexApplication() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        //WelcomeActivity为原启动页，注意不限进程,放在最上面，上面不要有其他初始化(MultiDex.install除外)
        if(KeepAliveManager.attachBaseContext(this, null)){
            //保活进程不要做任何操作
            return
        }
    }

    override fun onCreate() {
        super.onCreate()
        val isKeepProcess = KeepAliveManager.applicationCreate(this)
        if (isKeepProcess) {
            return
        }
        val isAttachJVMTI = JVMTIManager.attachJVMTI(this, true)
        Logger.e("isAttachJVMTI = $isAttachJVMTI")
        loge("process: ${processName()}")
        loge("light: ${UIModeManager.isLightMode}, dark: ${UIModeManager.isDarkMode}")
        UIModeManager.register(object : UIModeManager.OnUIModeChangeListener {
            override fun onUIModeChange(light: Boolean) {
                loge("light: $light")
            }
        })
        DataStorageManager.init(AppDataStorage(this))
    }

}