package com.xiaosw.api

import android.app.Application
import android.content.Context
import com.xiaosw.api.manager.ActivityLifeManager

/**
 * @ClassName [AndroidContext]
 * @Description
 *
 * @Date 2019-08-09.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
object AndroidContext {

    private var mApp: Application? = null

    internal fun init(context: Context) {
        mApp = context as? Application ?: context.applicationContext as? Application
        ActivityLifeManager.init(mApp)
    }

    @JvmStatic
    fun get() = mApp?.let {
        it
    } ?: throw NullPointerException("app is null, must call AndroidContext#init()!")

}