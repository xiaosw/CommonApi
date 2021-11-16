package com.xiaosw.api.reflect.compat

import android.annotation.TargetApi
import android.os.Build
import com.doudou.log.Logger
import com.xiaosw.api.reflect.ReflectCompat
import java.lang.Exception
import java.lang.reflect.Method
import java.util.concurrent.atomic.AtomicBoolean

/**
 * ClassName: [LimitHiddenCompat]
 * Description:
 *
 * Create by xsw at 2021/04/07 10:37.
 */
@TargetApi(Build.VERSION_CODES.P)
internal class LimitHiddenCompat : ReflectCompatDelegate {

    // VMRuntime
    private var sVMRuntime: Any? = null

    // VMRuntime#setHiddenApiExemptions(String[] signaturePrefixes);
    private var setHiddenApiExemptions : Method? = null

    private val mOriginalClassLoader = javaClass.classLoader

    private val mClassLoaderOffset by lazy {
        ReflectCompat.getField(Class::class.java, "classLoader")?.let {
            UnsafeHelper.objectFieldOffset(it)
        }
    }
    private val isModifyClassLoader by lazy {
        AtomicBoolean(false)
    }

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

    override fun compat() = exemptAll()

    private inline fun exempt(vararg signaturePrefixes: String) = internalExempt(*signaturePrefixes)

    private inline fun exemptAll() = exempt("L")

    private fun internalExempt(vararg signaturePrefixes: String) = try {
        modifyClassLoader()
        if (null == sVMRuntime || null == setHiddenApiExemptions) {
            false
        } else {
            setHiddenApiExemptions!!.invoke(sVMRuntime, signaturePrefixes)
            true
        }
    } catch (e: Exception) {
        Logger.e(e)
        false
    } finally {
        restoreClassLoader()
    }

    @Synchronized
    private inline fun modifyClassLoader() {
        mClassLoaderOffset?.let {
            UnsafeHelper.putObject(javaClass, it, null)
            isModifyClassLoader.compareAndSet(false, true)
        }
    }

    @Synchronized
    private inline fun restoreClassLoader() {
        if (!isModifyClassLoader.get()) {
            return
        }
        mClassLoaderOffset?.let {
            UnsafeHelper.putObject(javaClass, it, mOriginalClassLoader)
            isModifyClassLoader.compareAndSet(true, false)
        }
    }

}