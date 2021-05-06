package com.xiaosw.simple

import android.app.Application
import com.xiaosw.api.logger.Logger

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
    }

}