package com.xsw.track.jvmti

import android.content.Context

/**
 * ClassName: [JVMTI]
 * Description:
 *
 * Create by X at 2021/06/03 10:57.
 */
interface JVMTI {

    val isSupport: Boolean
        get() = false

    fun attachJVMTI(context: Context, forceReload: Boolean) : Boolean

    fun detachJVMTI()

}