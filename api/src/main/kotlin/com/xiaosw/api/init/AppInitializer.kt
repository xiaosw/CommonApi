package com.xiaosw.api.init

import android.content.Context
import androidx.annotation.Keep
import androidx.startup.Initializer
import com.xiaosw.api.AndroidContext
import com.xiaosw.api.manager.DensityManager

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
        DensityManager.init(context)
    }

    override fun dependencies(): MutableList<Class<out Initializer<Any>>> {
        return mutableListOf()
    }

}