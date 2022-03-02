package com.xiaosw.api.floating

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.xiaosw.api.AndroidContext
import com.xiaosw.api.floating.internal.GlobalFloatWindowLayout
import com.xiaosw.api.floating.internal.SingleFloatWindowLayout

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
        val key = "${owner.hashCode()}_${if (isGlobal) "" else ""}"
        mDelegates[key]?.let {
            return it
        }
        return if (isGlobal) {
            GlobalFloatWindowLayout().also {
                mDelegates[key] = it
            }
        } else {
            SingleFloatWindowLayout().also {
                mDelegates[key] = it
            }
        }
    }


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

}