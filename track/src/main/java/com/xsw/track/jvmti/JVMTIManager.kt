package com.xsw.track.jvmti

import android.content.Context
import com.xsw.track.jvmti.impl.JVMTIImpl

/**
 * ClassName: [JVMTIManager]
 * Description:
 *
 * Create by X at 2021/06/03 11:12.
 */
object JVMTIManager : JVMTI {

    private val sJVMTI by lazy {
        JVMTIImpl()
    }

    override val isSupport: Boolean
        get() = sJVMTI.isSupport

    override fun attachJVMTI(context: Context, forceReload: Boolean) =
        sJVMTI.attachJVMTI(context, forceReload)

    override fun detachJVMTI() = sJVMTI.detachJVMTI()

    ///////////////////////////////////////////////////////////////////////////
    // caller from native
    ///////////////////////////////////////////////////////////////////////////
    private fun objectAlloc() {

    }

    private fun objectFree() {

    }

}