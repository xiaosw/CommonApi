package com.xsw.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.Checkable

/**
 * ClassName: [StatusTextView]
 * Description:
 *
 * Create by X at 2021/08/02 10:16.
 */
class StatusTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr), Checkable {

    private var _checkableDelegate: CheckableDelegate? = null

    private fun checkableDelegate() : CheckableDelegate {
        if (_checkableDelegate == null) {
            _checkableDelegate = CheckableDelegate(this)
        }
        return _checkableDelegate as CheckableDelegate
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray? {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        checkableDelegate().mergeDrawableStates(drawableState)
        return drawableState
    }

    override fun setChecked(checked: Boolean) {
        checkableDelegate().isChecked = checked
    }

    override fun isChecked() = checkableDelegate().isChecked

    override fun toggle() = checkableDelegate().toggle()

}