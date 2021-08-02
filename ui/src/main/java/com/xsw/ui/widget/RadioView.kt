package com.xsw.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.Checkable
import androidx.constraintlayout.widget.ConstraintLayout
import com.xiaosw.api.extend.parseAttrs
import com.xsw.ui.R

/**
 * ClassName: [RadioView]
 * Description:
 *
 * Create by X at 2021/07/30 17:24.
 */
class RadioView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var isChecked = false
        private set

    init {
        attrs?.parseAttrs(context, R.styleable.RadioView) {
            isChecked = getBoolean(R.styleable.RadioView_android_checked, false)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        checkedChangeLocked(this)
    }

    override fun setOnClickListener(l: OnClickListener?) {
    }

    internal fun setOnInternalClickListener(l: OnClickListener) {
        super.setOnClickListener(l)
    }

    internal fun onCheckedChange(checked: Boolean) {
        if (isChecked == checked) {
            return
        }
        isChecked = checked
        checkedChangeLocked(this)
    }

    private fun checkedChangeLocked(group: ViewGroup) {
        val count = group.childCount
        for (index in 0 until count) {
            when(val v = getChildAt(index)) {
                is Checkable -> {
                    v.isChecked = isChecked
                }
                is ViewGroup -> {
                    checkedChangeLocked(v)
                }
            }
        }
    }

}