package com.xsw.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.constraintlayout.widget.ConstraintLayout

/**
 * ClassName: [RadioLayout]
 * Description:
 *
 * Create by X at 2021/07/30 17:12.
 */
class RadioLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var mRadioView: RadioView? = null
    var onCheckedChangeIntercept: OnCheckedChangeIntercept? = null
    var onCheckedChangeListener: OnCheckedChangeListener? = null

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        if (child is RadioView) {
            if (child.isChecked) {
                checked(child)
            }
            child.setOnInternalClickListener {
                checked(it as RadioView)
            }
        }
        super.addView(child, index, params)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        mRadioView?.let {
            checked(it)
        }
    }

    fun check(@IdRes id: Int) {
        if (id == NO_ID || id == mRadioView?.id) {
            return
        }
        (findViewById<View>(id) as? RadioView)?.let {
            checked(it)
        }
    }

    fun checked(radioView: RadioView) {
        if (mRadioView == radioView) {
            return
        }
        radioView?.let {
            val intercept = onCheckedChangeIntercept?.onCheckedChangeIntercept(mRadioView, it) ?: false
            if (intercept) {
                return
            }
            mRadioView = it
            val count = childCount
            for (index in 0 until count) {
                (getChildAt(index) as? RadioView)?.let { child ->
                    child.onCheckedChange(child == mRadioView)
                }
            }
            onCheckedChangeListener?.onCheckedChanged(this@RadioLayout, it)
        }
    }

    interface OnCheckedChangeListener {
        fun onCheckedChanged(layout: RadioLayout, radioView: RadioView)
    }

    interface OnCheckedChangeIntercept {
        fun onCheckedChangeIntercept(from: RadioView?, to: RadioView) : Boolean
    }
}