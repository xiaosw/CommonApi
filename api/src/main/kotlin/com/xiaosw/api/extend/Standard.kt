package com.xiaosw.api.extend

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import com.xiaosw.api.AndroidContext
import com.xiaosw.api.exception.TryCatchException
import com.xiaosw.api.logger.Logger
import com.xiaosw.api.logger.report.ReportManager
import com.xiaosw.api.util.EnvironmentUtil
import com.xiaosw.api.util.ScreenUtil
import java.io.File
import java.lang.Exception
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.jvm.internal.Intrinsics


/**
 * @Description
 *
 * @Date 2019-04-26.
 * @Author xiaosw<xiaosw0802@163.com>.
 */


/**
 * 安全执行代码块，即使代码异常也不抛错。
 */
@JvmOverloads
inline fun <T, R> T.tryCatch(
    errorMessage: String = ""
    , def: R? = null
    , showException: Boolean = true
    , block: (T) -> R
) : R? {
    try {
        return block(this)
    } catch (e: Throwable) {
        if (showException) {
            Logger.e("tryCatch: errorMessage = $errorMessage", throwable = e)
        }
    }
    return def
}

/**
 * 安全执行代码块，即使代码异常也不抛错，并且上报服务器。
 */
@JvmOverloads
inline fun <T, R> T.tryCatchAndReport(
    errorMessage: String = ""
    , def: R? = null
    , block: (T) -> R
) : R? {
    try {
        return block(this)
    } catch (e: Throwable) {
        Logger.e("tryCatch: errorMessage = $errorMessage", throwable = e)
        ReportManager.reportThrowable(
            TryCatchException(
                errorMessage,
                e
            )
        )
    }
    return def
}

@JvmOverloads
inline fun Any?.isNull() = null == this

@JvmOverloads
inline fun Any?.isNotNull() : Boolean {
    return !isNull()
}

@JvmOverloads
inline fun Any?.areEqual(second: Any?, ignoreCase: Boolean = false) : Boolean {
    if (this is String && second is String) {
        return equals(second, ignoreCase)
    }
    return Intrinsics.areEqual(this, second)
}

@JvmOverloads
inline fun Collection<*>?.isNull(onlyNull: Boolean = false) : Boolean {
    this?.let {
        if (onlyNull) {
            return false
        }
        return it.isEmpty()
    }
    return true
}

@JvmOverloads
inline fun Map<*, *>?.isNull(onlyNull: Boolean = false) : Boolean {
    this?.let {
        if (onlyNull) {
            return false
        }
        return it.isEmpty()
    }
    return true
}

@JvmOverloads
inline fun String?.isNull(useTrim: Boolean = false, ignoreNull: Boolean = true) : Boolean {
    this?.let {
        var arg = it
        if (useTrim) {
            arg = it.trim()
        }
        var isEmpty = arg.isEmpty()
        if (isEmpty) {
            return true
        }
        if (!ignoreNull) {
            return false
        }
        return arg.equals("null", true)
    }
    return true
}

inline fun File?.delete() {
    this?.let {
        if (isFile) {
            EnvironmentUtil.deleteFile(this)
        } else {
            EnvironmentUtil.deleteDir(this)
        }
    }
}

inline fun <T : TypedArray?> T?.use(block: T.() -> Unit) {
    this?.run {
        try {
            block(this)
        } finally {
            tryCatch(showException = false) {
                recycle()
            }
        }
    }
}

inline fun AttributeSet?.parseAttrs(context: Context, attrs: IntArray, block: TypedArray.() -> Unit) {
    context.obtainStyledAttributes(this, attrs).use {
        block(this)
    }
}

inline fun <T : Canvas?> T?.save(block: T.() -> Unit) {
    this?.run {
        try {
            save()
            block(this)
        } finally {
            tryCatch(showException = false) {
                restore()
            }
        }
    }
}

inline fun View.dp2px(dp: Float) = ScreenUtil.dp2px(context, dp)
inline fun View.dp2sp(dp: Float) = ScreenUtil.dp2sp(context, dp)
inline fun View.px2dp(px: Float) = ScreenUtil.px2dp(context, px)
inline fun View.sp2px(sp: Float) = ScreenUtil.sp2px(context, sp)
inline fun View.sp2dp(sp: Float) = ScreenUtil.sp2dp(context, sp)
inline fun View.px2sp(px: Float) = ScreenUtil.px2sp(context, px)

inline fun Bitmap.toDrawable(
    context: Context? = AndroidContext.get()
) : Drawable? {
    return this?.let { bitmap ->
        context?.let {
            BitmapDrawable(it.resources, bitmap)
        } ?: BitmapDrawable(bitmap)
    } ?: null
}

inline fun Drawable.toBitmap() : Bitmap? {
    if (this is BitmapDrawable) {
        return bitmap
    }
    return this?.let {
        val config = if (opacity != PixelFormat.OPAQUE) {
            Bitmap.Config.ARGB_8888
        } else {
            Bitmap.Config.RGB_565
        }
        Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, config)?.also {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            draw(Canvas(it))
        }
    } ?: null
}

inline fun CharSequence?.safe2Int(def: Int = 0) : Int {
    return try {
        if (!isNullOrEmpty()) {
            Integer.parseInt(toString())
        } else {
            def
        }
    } catch (e: Exception) {
        Logger.e(e)
        def
    }
}
inline fun Number?.safe2Int(def: Int = 0) : Int {
    return try {
        this?.toInt() ?: def
    } catch (e: Exception) {
        Logger.e(e)
        def
    }
}

inline fun CharSequence?.safe2Long(def: Long = 0L) : Long {
    return try {
        if (!isNullOrEmpty()) {
            java.lang.Long.parseLong(toString())
        } else {
            def
        }
    } catch (e: Exception) {
        Logger.e(e)
        def
    }
}

inline fun Number?.safe2Long(def: Long = 0) : Long {
    return try {
        this?.toLong() ?: def
    } catch (e: Exception) {
        Logger.e(e)
        def
    }
}

inline fun CharSequence?.safe2Short(def: Short = 0) : Short {
    return try {
        if (!isNullOrEmpty()) {
            java.lang.Short.parseShort(toString())
        } else {
            def
        }
    } catch (e: Exception) {
        Logger.e(e)
        def
    }
}

inline fun Number?.safe2Short(def: Short = 0) : Short {
    return try {
        this?.toShort() ?: def
    } catch (e: Exception) {
        Logger.e(e)
        def
    }
}

inline fun CharSequence?.safe2Byte(def: Byte = 0) : Byte {
    return try {
        if (!isNullOrEmpty()) {
            java.lang.Byte.parseByte(toString())
        } else {
            def
        }
    } catch (e: Exception) {
        Logger.e(e)
        def
    }
}

inline fun Number?.safe2Byte(def: Byte = 0) : Byte {
    return try {
        this?.toByte() ?: def
    } catch (e: Exception) {
        Logger.e(e)
        def
    }
}

inline fun CharSequence?.safe2Float(def: Float = 0f) : Float {
    return try {
        if (!isNullOrEmpty()) {
            java.lang.Float.parseFloat(toString())
        } else {
            def
        }
    } catch (e: Exception) {
        Logger.e(e)
        def
    }
}

inline fun Number?.safe2Float(def: Float = 0f) : Float {
    return try {
        this?.toFloat() ?: def
    } catch (e: Exception) {
        Logger.e(e)
        def
    }
}

inline fun CharSequence?.safe2Double(def: Double = 0.0) : Double {
    return try {
        if (!isNullOrEmpty()) {
            java.lang.Double.parseDouble(toString())
        } else {
            def
        }
    } catch (e: Exception) {
        Logger.e(e)
        def
    }
}

inline fun Number?.safe2Double(def: Double = 0.0) : Double {
    return try {
        this?.toDouble() ?: def
    } catch (e: Exception) {
        Logger.e(e)
        def
    }
}

inline fun CharSequence?.safe2Boolean(def: Boolean = false) : Boolean {
    return try {
        if (!isNullOrEmpty()) {
            java.lang.Boolean.parseBoolean(toString())
        } else {
            def
        }
    } catch (e: Exception) {
        Logger.e(e)
        def
    }
}

fun CharSequence?.trimEmptyOrNullChar(): Boolean {
    return this?.let {
        val t = it.trim()
        t.isEmpty() || t.toString().toLowerCase() == "null"
    } ?: true
}

inline fun measureTimeMillis(
    ownerClazz: Class<*>? = null,
    tag: String? = null,
    showLog: Boolean = true,
    block: () -> Unit
): Long {
    val start = System.currentTimeMillis()
    block()
    return (System.currentTimeMillis() - start).also { time ->
        if (showLog && Logger.isEnable()) {
            val owner = ownerClazz?.let { clazz ->
                tag?.let {
                    "${clazz.simpleName}#$tag "
                } ?: "${clazz.simpleName}# "
            } ?: (tag ?: "")
            Logger.i("${owner}use【${time}ms】in【${Thread.currentThread().name}】thread.")
        }
    }
}

inline fun isMainThread() = Looper.getMainLooper().thread == Thread.currentThread()

inline fun <reified T : Any> Any.optionalImpl() : T {
    val clazz = T::class.java
    val interfaces = if (clazz.isInterface) {
        arrayOf(clazz)
    } else {
        clazz.interfaces
    }
    val handler = InvocationHandler { _, _, _ -> }
    return Proxy.newProxyInstance(javaClass.classLoader, interfaces, handler) as T
}