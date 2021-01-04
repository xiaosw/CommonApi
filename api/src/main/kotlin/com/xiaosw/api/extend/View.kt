package com.xiaosw.api.extend

import android.app.Activity
import android.content.Context
import android.os.Build
import android.text.Html
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.xiaosw.api.helper.OnPreventFastClickListener
import com.xiaosw.api.logger.Logger

/**
 * @ClassName {@link View}
 * @Description
 *
 * @Date 2018-06-01.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
fun View.onClick(click: () -> Unit) {
    setOnClickListener(object : OnPreventFastClickListener {
        override var lastClickTime = 0L

        override fun onSingleClick(view: View) {
            click()
        }

    })
}

inline fun View.setVisibilityCompat(v: Int) {
    if (visibility != v) {
        visibility = v
    } else {
        Logger.w("setVisibilityCompat: same as the initial value!")
    }
}

// 全局 记录最后一次点击事件
var lastClickTime = 0L

inline fun View.checkClick(block: () -> Unit) {
    val timeGap = System.currentTimeMillis() - lastClickTime
    if (timeGap >= 300) {
        block()
    } else {
        Logger.w("checkClick: Click interval($timeGap) is too small!")
    }
    lastClickTime = System.currentTimeMillis()
}

@JvmOverloads
inline fun TextView.setHtmlText(htmlText: String?, flags: Int = Html.FROM_HTML_MODE_LEGACY) {
    text = htmlText?.let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(htmlText, flags)
        } else {
            Html.fromHtml(htmlText)
        }
    } ?: htmlText
}

@JvmOverloads
inline fun TextView.isEmpty(isTrim: Boolean = false) : Boolean {
    if (TextUtils.isEmpty(if (isTrim) text.trim() else text)) {
        return true
    }
    return false
}

@JvmOverloads
inline fun TextView.isNotEmpty(isTrim: Boolean = false) = !isEmpty(isTrim)

inline fun View.findActivity() : Activity? {
    if (isNull() || context.isNull()) {
        return null
    }
    var ctx = context
    if (context.javaClass.name.contains("com.android.internal.policy.DecorContext")) {
        tryCatch {
            val mPhoneWindowField = context.javaClass.getDeclaredField("mPhoneWindow")
            if (mPhoneWindowField.isNull()) {
                return null
            }
            mPhoneWindowField.isAccessible = true
            val mPhoneWindow = mPhoneWindowField.get(it)
            if (mPhoneWindow.isNull()) {
                return null
            }
            val getContextMethod = mPhoneWindow.javaClass.getDeclaredMethod("getContext")
            if (getContextMethod.isNull()) {
                return null
            }
            (getContextMethod.invoke(mPhoneWindow) as? Context)?.let { getContext ->
                ctx = getContext
            }
        }
    }
    return ctx?.findActivity() ?: null
}
