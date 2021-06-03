package com.xsw.track.jvmti.impl

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Debug
import com.xiaosw.api.extend.tryCatch
import com.xsw.track.jvmti.JVMTI
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.atomic.AtomicBoolean

/**
 * ClassName: [JVMTIImpl]
 * Description:
 *
 * Create by X at 2021/06/03 10:46.
 */
internal class JVMTIImpl : JVMTI {

    private var isAttached = AtomicBoolean(false)

    override val isSupport: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    override fun attachJVMTI(context: Context, forceReload: Boolean): Boolean {
        if (!isSupport) {
            return false
        }
        val javaAttach = javaAttach(context, forceReload)
        if (!javaAttach) {
            return false
        }
        return nativeAttach().also {
            isAttached.compareAndSet(false, it)
        }
    }

    override fun detachJVMTI() {
        if (!isAttached.get() || !isSupport) {
            return
        }
        nativeDetach()
        isAttached.compareAndSet(true, false)
    }

    @TargetApi(Build.VERSION_CODES.O)
    private inline fun javaAttach(context: Context, forceReload: Boolean) : Boolean {
        return tryCatch(def = false) {
            val findLibraryMethod =
                ClassLoader::class.java.getDeclaredMethod("findLibrary", String::class.java).also {
                    it.isAccessible = true
                }
            val jvmtiAgentPath = findLibraryMethod.invoke(javaClass.classLoader, SO_NAME) as? String ?: ""
            val jvmtiAgentFullName = jvmtiAgentPath.substring(jvmtiAgentPath.lastIndexOf("/") + 1)

            val filesDir = context.filesDir
            val jvmtiMonitorDir = File(filesDir, "jvm-monitor")
            if (!jvmtiMonitorDir.exists()) {
                jvmtiMonitorDir.mkdirs()
            }
            val destFile = File(jvmtiMonitorDir, jvmtiAgentFullName)
            val destPath = destFile.absolutePath
            if (destFile.exists()) {
                if (forceReload) {
                    destFile.delete()
                    Files.copy(Paths.get(jvmtiAgentPath), Paths.get(destPath))
                }
            } else {
                Files.copy(Paths.get(jvmtiAgentPath), Paths.get(destPath))
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                Debug.attachJvmtiAgent(destPath, null, javaClass.classLoader)
            } else {
                Class.forName("dalvik.system.VMDebug")
                    .getDeclaredMethod("attachJvmtiAgent", String::class.java)
                    .also {
                        it.isAccessible = true
                        it.invoke(null, destFile)
                    }
            }
            true
        } ?: false
    }

    private external fun nativeAttach() : Boolean

    private external fun nativeDetach()

    companion object {
        private const val SO_NAME = "jvm-monitor"
    }
}