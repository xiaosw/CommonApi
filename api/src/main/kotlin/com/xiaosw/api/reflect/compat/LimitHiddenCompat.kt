package com.xiaosw.api.reflect.compat

import android.annotation.TargetApi
import android.os.Build
import com.xiaosw.api.logger.Logger
import com.xiaosw.api.reflect.ReflectCompat
import java.lang.Exception
import java.lang.reflect.Method

/**
 * ClassName: [LimitHiddenCompat]
 * Description:
 *
 * Create by xsw at 2021/04/07 10:37.
 */
@TargetApi(Build.VERSION_CODES.P)
internal class LimitHiddenCompat : ReflectCompatDelegate() {

    // VMRuntime
    private var sVMRuntime: Any? = null
    // VMRuntime#setHiddenApiExemptions(String[] signaturePrefixes);
    private var setHiddenApiExemptions : Method? = null

    init {
        val clazz = Class::class.java
        // Class#forName
        val forName = clazz.getDeclaredMethod("forName", String::class.java).also {
            it.isAccessible = true
        }

        // Class#getDeclaredMethod
        val getDeclaredMethod = clazz.getDeclaredMethod(
            "getDeclaredMethod",
            String::class.java,
            arrayOf<Class<*>>()::class.java
        )
        try {
            val vMRuntimeClazz = forName.invoke(null, "dalvik.system.VMRuntime")

            setHiddenApiExemptions = (getDeclaredMethod.invoke(vMRuntimeClazz
                , "setHiddenApiExemptions"
                , arrayOf<Class<*>>(
                    Array<String>::class.java
                )
            ) as Method).also {
                it.isAccessible = true
            }

            (getDeclaredMethod.invoke(vMRuntimeClazz, "getRuntime", null) as Method).also {
                it.isAccessible = true
                sVMRuntime = it.invoke(null)
            }
        } catch (t: Throwable) {
            Logger.e(t)
        }

    }

    override fun compat(className: String) = ReflectCompat.forName(className)?.let {
        compat(it)
    } ?: false

    override fun compat(clazz: Class<*>) = try {
        when {
            exemptAll() -> {
                Logger.e("exemptAll")
                true
            }

            replaceClassLoader(clazz) -> {
                Logger.e("replaceClassLoader")
                true
            }

            else -> {
                Logger.e("field!")
                false
            }
        }
    } catch (e: Exception) {
        Logger.e(e)
        false
    }

    private inline fun exempt(vararg signaturePrefixes: String) = internalExempt(*signaturePrefixes)

    private inline fun exemptAll() = exempt("L")

    private fun internalExempt(vararg signaturePrefixes: String) = try {
        if (null == sVMRuntime || null == setHiddenApiExemptions) {
            false
        } else {
            setHiddenApiExemptions!!.invoke(sVMRuntime, signaturePrefixes)
            true
        }
    } catch (e: Exception) {
        Logger.e(e)
        false
    }

}