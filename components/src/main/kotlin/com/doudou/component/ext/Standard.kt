package com.doudou.component.ext

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import com.doudou.log.Logger
import com.doudou.log.loge
import com.xiaosw.api.AndroidContext
import com.xiaosw.api.exception.TryCatchException
import com.xiaosw.api.global.GlobalWeakHandler
import com.xiaosw.api.logger.report.ReportManager
import com.xiaosw.api.util.EnvironmentUtil
import com.xiaosw.api.util.ScreenUtil
import java.io.File
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy
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
            loge("tryCatch: errorMessage = $errorMessage", tr = e)
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
        Logger.e("tryCatch: errorMessage = $errorMessage", tr = e)
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

inline fun Bitmap?.toDrawable(
    context: Context? = AndroidContext.get()
) : Drawable? {
    return this?.let { bitmap ->
        context?.let {
            BitmapDrawable(it.resources, bitmap)
        } ?: BitmapDrawable(bitmap)
    } ?: null
}

@JvmOverloads
fun Bitmap?.scaleTo(dstWidth: Float, dstHeight: Float, useRatio: Boolean = true) = this?.let {
    if (useRatio) {
        return@let Bitmap.createScaledBitmap(it, dstWidth.toInt(), dstHeight.toInt(), false)
    }
    if (dstWidth === width.toFloat() && dstHeight === height.toFloat()) {
        return this
    }
    val sx = dstWidth / width
    val sy = dstHeight / height
    val matrix = Matrix().apply {
        setScale(sx, sy)
    }
    val out = Bitmap.createBitmap((width * sx).toInt(), (height * sy).toInt(), config)
    Canvas(out).drawBitmap(this, matrix, Paint(Paint.ANTI_ALIAS_FLAG))
    return@let out
} ?: null

@JvmOverloads
fun Bitmap?.roundTo(rx: Float, ry: Float) = this?.let {
    if (rx < 0 || ry < 0) {
        return@let it
    }
    val rectF = RectF(0F, 0F, it.width * 1F, it.height * 1F)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val out = Bitmap.createBitmap(width, height, config)
    with(Canvas(out)) {
        drawRoundRect(rectF, rx, ry, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        drawBitmap(this@roundTo, 0F, 0f, paint)
    }
    out
} ?: null

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
    return tryCatch {
        if (!isNullOrEmpty()) {
            Integer.parseInt(toString())
        } else {
            def
        }
    } ?: def
}
inline fun Number?.safe2Int(def: Int = 0) : Int {
    return tryCatch {
        this?.toInt()
    } ?: def
}

inline fun CharSequence?.safe2Long(def: Long = 0L) : Long {
    return tryCatch {
        if (!isNullOrEmpty()) {
            java.lang.Long.parseLong(toString())
        } else {
            def
        }
    } ?: def
}

inline fun Number?.safe2Long(def: Long = 0) : Long {
    return tryCatch {
        this?.toLong()
    } ?: def
}

inline fun CharSequence?.safe2Short(def: Short = 0) : Short {
    return tryCatch {
        if (!isNullOrEmpty()) {
            java.lang.Short.parseShort(toString())
        } else {
            def
        }
    } ?: def
}

inline fun Number?.safe2Short(def: Short = 0) : Short {
    return tryCatch {
        this?.toShort()
    } ?: def
}

inline fun CharSequence?.safe2Byte(def: Byte = 0) : Byte {
    return tryCatch {
        if (!isNullOrEmpty()) {
            java.lang.Byte.parseByte(toString())
        } else {
            def
        }
    } ?: def
}

inline fun Number?.safe2Byte(def: Byte = 0) : Byte {
    return tryCatch {
        this?.toByte()
    } ?: def
}

inline fun CharSequence?.safe2Float(def: Float = 0f) : Float {
    return tryCatch {
        if (!isNullOrEmpty()) {
            java.lang.Float.parseFloat(toString())
        } else {
            def
        }
    } ?: def
}

inline fun Number?.safe2Float(def: Float = 0f) : Float {
    return tryCatch {
        this?.toFloat()
    } ?: def
}

inline fun CharSequence?.safe2Double(def: Double = 0.0) : Double {
    return tryCatch {
        if (!isNullOrEmpty()) {
            java.lang.Double.parseDouble(toString())
        } else {
            def
        }
    } ?: def
}

inline fun Number?.safe2Double(def: Double = 0.0) : Double {
    return tryCatch(def = def) {
        this?.toDouble()
    } ?: def
}

inline fun CharSequence?.safe2Boolean(def: Boolean = false) : Boolean {
    return tryCatch {
        if (!isNullOrEmpty()) {
            java.lang.Boolean.parseBoolean(toString())
        } else {
            def
        }
    } ?: def
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
        if (showLog && Logger.enable) {
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

inline fun runOnUiThread(crossinline block: () -> Unit) {
    if (isMainThread()) {
        block.invoke()
        return
    }
    GlobalWeakHandler.mainHandler.post {
        block.invoke()
    }
}

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