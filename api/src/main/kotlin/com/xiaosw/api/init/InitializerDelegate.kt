package com.xiaosw.api.init

import com.xiaosw.api.logger.Logger
import java.util.concurrent.atomic.AtomicBoolean

/**
 * ClassName: [InitializerDelegate]
 * Description:
 *
 * Create by X at 2021/06/30 16:30.
 */
open class InitializerDelegate {

    internal val isInitializer = AtomicBoolean()

    protected open fun onInitBefore() {}

    protected open fun onInitAfter(isInitializer: Boolean) {}

    fun isInitializer() = isInitializer.get()

    internal inline fun showTime(time: Long) {
        if (Logger.isEnable()) {
            Logger.i("${javaClass.simpleName}#init【${isInitializer()}】, use【${time}ms】" +
                    "in【${Thread.currentThread().name}】thread.")
        }
    }

}