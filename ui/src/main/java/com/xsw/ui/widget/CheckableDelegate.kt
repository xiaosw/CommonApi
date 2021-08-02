package com.xsw.ui.widget

import android.view.View
import android.widget.Checkable

/**
 * ClassName: [CheckableDelegate]
 * Description:
 *
 * Create by X at 2021/08/02 18:12.
 */
class CheckableDelegate(private val owner: View) : Checkable {

    private var _checked = false

    override fun setChecked(checked: Boolean) {
        _checked = checked
        owner.refreshDrawableState()
    }

    override fun isChecked() = _checked

    override fun toggle() {
        isChecked = !_checked
    }

    fun mergeDrawableStates(state: IntArray?): IntArray? {
        if (_checked) {
            state?.let {
                var i = state.size - 1
                while (i >= 0 && state[i] == 0) {
                    i--
                }
                val additionalState = CHECKED_STATE_SET
                System.arraycopy(additionalState, 0, state, i + 1, additionalState.size)
            }
        }
        return state
    }

    companion object {
        private val CHECKED_STATE_SET by lazy {
            intArrayOf(
                android.R.attr.state_checked
            )
        }
    }

}