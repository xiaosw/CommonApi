package com.xiaosw.api.init

import com.xiaosw.api.extend.measureTimeMillis
import com.xiaosw.api.extend.tryCatch

/**
 * ClassName: [Initializer1Delegate]
 * Description:
 *
 * Create by X at 2021/06/30 16:34.
 */
abstract class Initializer1Delegate<P> : InitializerDelegate() {

    @Synchronized fun init(p: P? = null) {
        if (isInitializer()) {
            return
        }
        measureTimeMillis(showLog = false) {
            tryCatch {
                onInitBefore()
            }
            tryCatch {
                if (onInit(p)) {
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

    protected abstract fun onInit(p: P?) : Boolean

}