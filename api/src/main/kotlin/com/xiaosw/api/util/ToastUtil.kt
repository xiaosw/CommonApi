package com.xiaosw.api.util

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import com.xiaosw.api.AndroidContext
import com.xiaosw.api.R
import com.xiaosw.api.extend.tryCatch
import java.lang.ref.WeakReference

/**
 * @ClassName [ToastUtil]
 * @Description
 *
 * @Date 2019-04-26.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
object ToastUtil {

    private var mLastToast: WeakReference<Toast>? = null

    private fun cancelLastIfNeeded() {
        mLastToast?.get()?.tryCatch {
            it.cancel()
            mLastToast = null
        }
    }

    @JvmStatic
    @JvmOverloads
    fun showToast(context: Context, textId: Int, duration: Int = Toast.LENGTH_SHORT) : Boolean {
        return showToast(context, context.getString(textId), duration)
    }

    @JvmStatic
    @JvmOverloads
    fun showToast(context: Context = AndroidContext.get()
                  , message: CharSequence
                  , duration: Int = Toast.LENGTH_SHORT
    ) : Boolean {
        var isShow = false
        tryCatch {
            cancelLastIfNeeded()
            Toast(context).apply {
                setGravity(Gravity.CENTER, 0, 0)
                setDuration(duration)
                view = TextView(context).apply {
                    setTextColor(Color.WHITE)
                    setBackgroundResource(R.drawable.background_toast)
                    textSize = 16f
                    text = message
                    gravity = Gravity.CENTER
                }
                show()
                mLastToast = WeakReference(this)
                isShow = true
            }
        }
        return isShow
    }

}