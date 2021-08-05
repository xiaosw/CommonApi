package com.xiaosw.api.init

import com.xiaosw.api.extend.measureTimeMillis
import com.xiaosw.api.extend.tryCatch


/**
 * ClassName: [Initializer3Delegate]
 * Description:
 *
 * Create by X at 2021/06/30 16:16
 */
abstract class Initializer3Delegate<P1, P2, P3> : InitializerDelegate() {

    @Synchronized fun init(p1: P1? = null, p2: P2? = null, p3: P3? = null) {
        if (isInitializer()) {
            return
        }
        measureTimeMillis(showLog = false) {
            tryCatch {
                onInitBefore()
            }
            tryCatch {
                if (onInit(p1, p2, p3)) {
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

    protected abstract fun onInit(p1: P1?, p2: P2?, p3: P3?) : Boolean

}