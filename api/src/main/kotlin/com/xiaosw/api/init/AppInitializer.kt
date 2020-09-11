package com.xiaosw.api.init

import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.Keep
import androidx.startup.Initializer
import com.xiaosw.api.AndroidContext
import com.xiaosw.api.BuildConfig
import com.xiaosw.api.config.AppConfig
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
class AppInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        AndroidContext.init(context)
        AppConfig.isDebug = BuildConfig.DEBUG
        ThreadManager.execute(ThreadManager.THREAD_TYPE_WORK, Runnable {
            context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
                ?.metaData?.let { metaData ->
                    DensityManager.init(context
                        , metaData.getFloat("APP_BASE_DP")
                        , metaData.getBoolean("APP_BASE_DP_BY_WIDTH")
                        , metaData.getBoolean("APP_BASE_DP_ENABLE"))
                }
        })
    }

    override fun dependencies(): MutableList<Class<out Initializer<Any>>> {
        return mutableListOf()
    }
}