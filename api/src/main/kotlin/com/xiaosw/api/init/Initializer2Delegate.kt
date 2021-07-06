package com.xiaosw.api.init

import com.xiaosw.api.extend.measureTimeMillis
import com.xiaosw.api.extend.tryCatch


/**
 * ClassName: [Initializer2Delegate]
 * Description:
 *
 * Create by X at 2021/06/30 16:34.
 */
abstract class Initializer2Delegate<P1, P2> : InitializerDelegate() {

    @Synchronized fun init(p1: P1? = null, p2: P2? = null) {
        if (isInitializer()) {
            return
        }
        measureTimeMillis(showLog = false) {
            tryCatch {
                onInitBefore()
            }
            tryCatch {
                if (onInit(p1, p2)) {
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

    protected abstract fun onInit(p1: P1?, p2: P2?) : Boolean

}