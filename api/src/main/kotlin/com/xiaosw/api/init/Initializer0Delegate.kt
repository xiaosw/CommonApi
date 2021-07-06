package com.xiaosw.api.init

import com.xiaosw.api.extend.measureTimeMillis
import com.xiaosw.api.extend.tryCatch


/**
 * ClassName: [Initializer0Delegate]
 * Description:
 *
 * Create by X at 2021/06/30 16:34.
 */
abstract class Initializer0Delegate : InitializerDelegate() {

    @Synchronized fun init() {
        if (isInitializer()) {
            return
        }
        measureTimeMillis(showLog = false) {
            tryCatch {
                onInitBefore()
            }
            tryCatch {
                if (onInit()) {
                    isInitializer.compareAndSet(false, true)
                }
            }
            tryCatch {
                onInitAfter(isInitializer.get())
            }
        }.also {
            showTime(it)
        }
    }

    protected abstract fun onInit() : Boolean

}