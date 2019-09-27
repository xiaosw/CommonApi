package com.xiaosw.api.util

import android.widget.TextView
import java.lang.StringBuilder


/**
 * @ClassName [StringUtil]
 * @Description
 *
 * @Date 2019-08-09.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
object StringUtil {

    @JvmStatic
    fun isNotEmpty(text: String?) = text?.isNotEmpty() ?: true

    @JvmStatic
    fun isEmpty(text: String?) = text?.isEmpty() ?: true

    @JvmStatic
    @JvmOverloads
    fun getText(textView: TextView?, useTrim: Boolean = true) = textView?.let {
        if (useTrim) {
            it.text.toString().trim()
        } else {
            it.text.toString()
        }
    } ?: ""

}