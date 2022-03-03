package com.xiaosw.api.floating

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.xiaosw.api.AndroidContext
import com.xiaosw.api.extend.isNotNull
import com.xiaosw.api.floating.internal.GlobalFloatWindowLayout
import com.xiaosw.api.floating.internal.SingleFloatWindowLayout
import com.xiaosw.api.util.Utils

/**
 * ClassName: [FloatWindowManager]
 * Description:
 *
 * Create by X at 2022/03/01 15:51.
 */
object FloatWindowManager {

    private val mDelegates by lazy {
        mutableMapOf<Any, FloatWindowController?>()
    }

    @JvmStatic
    @JvmOverloads
    fun get(owner: Any, isGlobal: Boolean = true) : FloatWindowController {
        val key = createKey(owner, isGlobal)
        mDelegates[key]?.let {
            return it
        }
        return if (isGlobal) {
            GlobalFloatWindowLayout(owner).also {
                mDelegates[key] = it
            }
        } else {
            SingleFloatWindowLayout(owner).also {
                mDelegates[key] = it
            }
        }
    }

    @JvmOverloads
    @JvmStatic
    fun hasFloatingWindow(owner: Any, isGlobal: Boolean = true) =
        mDelegates[createKey(owner, isGlobal)].isNotNull()

    fun canDrawOverlays() : Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && !Settings.canDrawOverlays(AndroidContext.get())) {
            return false
        }
        return true
    }

    fun openDrawOverlays() {
        with(AndroidContext.get()) {
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                data = Uri.parse("package:$packageName")
            })
        }
    }

    internal fun remove(owner: Any, isGlobal: Boolean = true) {
        val key = createKey(owner, isGlobal)
        mDelegates.remove(key)
    }

    private fun createKey(owner: Any, isGlobal: Boolean) =
        "${owner.hashCode()}_${if (isGlobal) "global" else "single"}"

}