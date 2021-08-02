package com.xiaosw.api

import android.app.Application
import android.content.Context
import androidx.annotation.Keep
import com.xiaosw.api.extend.isMainProcess
import com.xiaosw.api.init.Initializer1Delegate
import com.xiaosw.api.manager.ActivityLifeManager
import com.xiaosw.api.manager.UIModeManager

/**
 * @ClassName [AndroidContext]
 * @Description
 *
 * @Date 2019-08-09.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
@Keep
object AndroidContext : Initializer1Delegate<Context>() {

    val isMainProcess by lazy {
        get().isMainProcess()
    }

    private var mApp: Application? = null

    override fun onInit(context: Context?) =
        (context as? Application ?: context?.applicationContext as? Application)?.let {
            mApp = it
            ActivityLifeManager.init(it)
            UIModeManager.init(it)
            true
        } ?: false

    @JvmStatic
    fun get() = mApp?.let {
        it
    } ?: throw UninitializedPropertyAccessException("app is null, must call AndroidContext#init(Context)!")

}