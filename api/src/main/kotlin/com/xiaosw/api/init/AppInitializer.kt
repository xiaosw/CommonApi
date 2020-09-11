package com.xiaosw.api.init

import android.content.Context
import androidx.annotation.Keep
import androidx.startup.Initializer
import com.xiaosw.api.AndroidContext
import com.xiaosw.api.BuildConfig
import com.xiaosw.api.config.AppConfig
import com.xiaosw.api.logger.Logger
import com.xiaosw.api.manager.DensityManager
import com.xiaosw.api.manager.ThreadManager

/**
 * @ClassName [AppInitializer]
 * @Description
 *
 * @Date 2019-08-09.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
@Keep
internal class AppInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        AndroidContext.init(context)
        AppConfig.isDebug = BuildConfig.DEBUG
        ThreadManager.execute(ThreadManager.THREAD_TYPE_WORK, Runnable {
            DensityManager.init(context)
        })
    }

    override fun dependencies(): MutableList<Class<out Initializer<Any>>> {
        return mutableListOf()
    }

}