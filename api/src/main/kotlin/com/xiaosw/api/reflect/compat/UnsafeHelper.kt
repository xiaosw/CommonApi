package com.xiaosw.api.reflect.compat

import com.xiaosw.api.extend.tryCatch
import com.xiaosw.api.reflect.ReflectCompat
import java.lang.reflect.Field

/**
 * ClassName: [UnsafeHelper]
 * Description:
 *
 * Unsafe: http://androidxref.com/9.0.0_r3/xref/libcore/ojluni/src/main/java/sun/misc/Unsafe.java
 *
 * Create by xsw at 2021/04/07 14:49.
 */
internal object UnsafeHelper {

    private val mUnsafeClazz by lazy {
        Class.forName("sun.misc.Unsafe")
    }

    private val mUnsafe by lazy {
        val unsafe = tryCatch {
            ReflectCompat.getMethod(mUnsafeClazz, "getUnsafe")?.let {
                ReflectCompat.invokeStaticMethod(it)
            }
        } ?: tryCatch {
            ReflectCompat.getField(mUnsafeClazz, "THE_ONE")?.get(null)!!
        } ?: tryCatch {
            ReflectCompat.getField(mUnsafeClazz, "theUnsafe")?.get(null)!!
        }
    }

    private val mAllocateInstance by lazy {
        ReflectCompat.getMethod(mUnsafeClazz, "allocateInstance", true, Class::class.java)!!
    }

    private val mObjectFieldOffset by lazy {
        ReflectCompat.getMethod(mUnsafeClazz, "objectFieldOffset", true, Field::class.java)!!
    }

    private val mPutLong by lazy {
        ReflectCompat.getMethod(
            mUnsafeClazz,
            "putLong",
            true,
            Any::class.java,
            Long::class.java,
            Long::class.java
        )!!
    }

    private val mPutInt by lazy {
        ReflectCompat.getMethod(
            mUnsafeClazz,
            "putInt",
            true,
            Any::class.java,
            Long::class.java,
            Int::class.java
        )!!
    }

    private val mPutObject by lazy {
        ReflectCompat.getMethod(
            mUnsafeClazz,
            "putObject",
            true,
            Any::class.java,
            Long::class.java,
            Any::class.java
        )!!
    }

    private val mArrayIndexScale by lazy {
        ReflectCompat.getMethod(mUnsafeClazz, "arrayIndexScale", true, Class::class.java)!!
    }

    private val mArrayBaseOffset by lazy {
        ReflectCompat.getMethod(mUnsafeClazz, "arrayBaseOffset", true, Class::class.java)!!
    }

    fun <T> allocateInstance(clazz: Class<T>) =
        ReflectCompat.invokeMethod(mAllocateInstance, mUnsafe, clazz) as T

    fun objectFieldOffset(field: Field) =
        ReflectCompat.invokeMethod(mObjectFieldOffset, mUnsafe, field) as? Long ?: 0L

    fun putLong(targetObj: Any?, offset: Long, newValue: Long) = ReflectCompat.invokeMethod(
        mObjectFieldOffset,
        mUnsafe,
        targetObj,
        offset,
        newValue
    ) as? Long
        ?: 0L

    fun putInt(targetObj: Any?, offset: Long, newValue: Int) = ReflectCompat.invokeMethod(
        mObjectFieldOffset,
        mUnsafe,
        targetObj,
        offset,
        newValue
    ) as? Long
        ?: 0L

    fun putObject(targetObj: Any?, offset: Long, newValue: Any?) = ReflectCompat.invokeMethod(
        mObjectFieldOffset,
        mUnsafe,
        targetObj,
        offset,
        newValue
    ) as? Long
        ?: 0L

    fun arrayIndexScale(clazz: Class<*>) =
        ReflectCompat.invokeMethod(mArrayIndexScale, mUnsafe, clazz) as? Int ?: 0

    fun arrayBaseOffset(clazz: Class<*>) =
        ReflectCompat.invokeMethod(mArrayBaseOffset, mUnsafe, clazz) as? Int ?: 0
}