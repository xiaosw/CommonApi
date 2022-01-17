package com.xiaosw.simple

import com.doudou.log.Logger
import me.ele.lancet.base.Origin
import me.ele.lancet.base.Scope
import me.ele.lancet.base.annotations.Proxy
import me.ele.lancet.base.annotations.TargetClass

/**
 * ClassName: [Lancent]
 * Description:
 *
 * Create by X at 2022/01/17 16:44.
 */
class Lancent {

    @TargetClass("android.app.Application", scope = Scope.LEAF)
    @Proxy("onCreate")
    fun onCreate() {
        Logger.e("Lancent#onCreate")
        Origin.callVoidThrowOne<Throwable>()
    }

}