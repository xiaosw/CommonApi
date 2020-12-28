package com.xsw.compat.start.delegate

import android.content.Context
import android.content.Intent
import android.os.Build
import com.xiaosw.api.AndroidContext
import com.xiaosw.api.extend.tryCatch

/**
 * @ClassName: [StartDelegate]
 * @Description:
 *
 * Created by admin at 2020-12-25
 * @Email xiaosw0802@163.com
 */
abstract class StartDelegate {

    val app = AndroidContext.get()

    fun startActivity(intent: Intent?) = intent?.let {
        tryCatch("startActivity", false) {
            startActivity(app, intent)
        }
    } ?: false

    internal abstract fun startActivity(context: Context, intent: Intent) : Boolean

    companion object {

        fun create() : StartDelegate {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return StartDelegateOImpl()
            }
            return StartDelegateImpl()
        }

    }
}