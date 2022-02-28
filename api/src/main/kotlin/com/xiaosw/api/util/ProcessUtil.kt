package com.xiaosw.api.util

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Process
import com.doudou.log.logi
import com.xiaosw.api.AndroidContext
import com.xiaosw.api.extend.trimEmptyOrNullChar
import com.xiaosw.api.extend.tryCatch
import com.xiaosw.api.hook.HookUtil
import com.xiaosw.api.hook.safeDeclaredMethod
import java.io.BufferedReader
import java.io.FileReader

/**
 * ClassName: [ProcessUtil]
 * Description:
 *
 * Create by X at 2022/02/28 17:11.
 */
object ProcessUtil {

    val processName by lazy {
        var pn = fromApplication()
        if (!pn.trimEmptyOrNullChar()) {
            return@lazy pn
        }
        pn = fromActivityThread()
        if (!pn.trimEmptyOrNullChar()) {
            return@lazy pn
        }
        pn = fromAMS()
        if (!pn.trimEmptyOrNullChar()) {
            return@lazy pn
        }
        fromProc()
    }

    private fun fromApplication() : String? = tryCatch {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return@tryCatch (Application::class.java.getMethod("getProcessName").invoke(null) as? String)?.let {
                logi {
                    "process name from application: $it"
                }
                it
            }
        }
        null
    }

    @SuppressLint("PrivateApi")
    private fun fromActivityThread() : String? = tryCatch {
        (HookUtil.safe2Class("android.app.ActivityThread")
            ?.safeDeclaredMethod("currentProcessName")
            ?.invoke(null) as? String)?.let {
            logi {
                "process name from activity thread: $it"
            }
            it
        }
    } ?: null

    private fun fromAMS() : String? = tryCatch {
        (AndroidContext.get()?.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager)?.let { am ->
            val myPid = Process.myPid()
            am.runningAppProcesses?.forEach { process ->
                if (process?.pid === myPid) {
                    return@tryCatch process.processName?.let {
                        logi {
                            "process name from AMS: $it"
                        }
                        it
                    }
                }
            }
        }
        null
    }

    private fun fromProc(): String? = tryCatch {
        BufferedReader(FileReader("/proc/${Process.myPid()}/cmdline")).use {
            it.readLine()?.replace("[^(a-zA-Z:._ \\-)]".toRegex(), "")?.let { pn ->
                logi {
                    "process name from proc: $pn"
                }
                pn
            }
        }
    } ?: null

}