package com.doudou.log

import android.app.Application
import android.content.Context
import java.util.concurrent.atomic.AtomicBoolean

/**
 * ClassName: [AndroidContext]
 * Description:
 *
 * Create by X at 2022/06/21 10:21.
 */
internal object AndroidContext {

    private val isInit =  AtomicBoolean(false)
    private lateinit var mApp: Application
    fun init(context: Context?) {
        if (null == context) {
            return
        }
        if (isInit.get()) {
            return
        }
        mApp = (context as? Application) ?: context?.applicationContext as Application
        isInit.set(true)
    }

    fun get() = mApp
}