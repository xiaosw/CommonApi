package com.xiaosw.api.helper

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.*
import com.xiaosw.api.extend.setVisibilityCompat

import com.xiaosw.api.logger.Logger
import com.xiaosw.api.util.StringUtil


/**
 * @ClassName [FormHelper]
 * @Description 表单检测帮助类。用于页面需要填写必填项才能下一步等操作。
 *
 * @Date 2018-02-24.
 * @Author xiaosw<xiaosw0802@163.com>.
 */

class FormHelper @JvmOverloads constructor(
    private val mNext: View? = null,
    enable: Boolean = false
) : CompoundButton.OnCheckedChangeListener {

    private val mConditions by lazy {
        mutableListOf<View>()
    }

    init {
        mNext?.isEnabled = enable
    }

    private fun addConditions(view: View) {
        mConditions.add(view)
        checkEnable()
    }

    @JvmOverloads
    fun add(input: Input?) : FormHelper {
        input?.let {
            add(it.input, if (it.labelEnable) it.label else null)
        } ?: Logger.w("add: Input is null!",
            TAG
        )
        return this
    }

    @JvmOverloads
    fun add(input: TextView?, label: View? = null, defStatus: Int = View.INVISIBLE) : FormHelper {
        input?.let {
            addConditions(it)
            val textWatcher = object : TextWatcher {

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    checkEnable()
                    label?.apply {
                        setVisibilityCompat(s?.toString().let { newText ->
                            if (StringUtil.isNotEmpty(newText)) View.VISIBLE else defStatus
                        })
                    }
                }

            }
            it.addTextChangedListener(textWatcher)
            it.setTag(it.id, textWatcher)
        } ?: Logger.w("add: EditText is null!",
            TAG
        )
        return this
    }

    fun add(checkBox: CheckBox?) : FormHelper {
        checkBox?.let {
            addConditions(checkBox)
            checkBox.setOnCheckedChangeListener(this)
        } ?: Logger.w("add: CheckBox is null!",
            TAG
        )
        return this

    }

    fun add(imageView: ImageView?) {
        imageView?.let {
            addConditions(imageView)
        } ?: Logger.w("add: TextView is null!",
            TAG
        )
    }

    fun remove(view: View?) {
        view?.apply {
            if (this is TextView) {
                (getTag(view.id) as? TextWatcher)?.let {
                    removeTextChangedListener(it)
                }
            }
            if (this is Input) {
                mConditions.remove(input)
            } else {
                mConditions.remove(this)
            }
            checkEnable()
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        checkEnable()
    }

    @JvmOverloads
    fun notifyDateChange() = checkEnable()

    private fun checkEnable() {
        mNext?.let {
            var isEnable = true
            for (conditionView in mConditions) {
                if (conditionView is Input) {
                    if (TextUtils.isEmpty(conditionView.input.text)) {
                        isEnable = false
                        break
                    }
                } else if (conditionView is CheckBox) {
                    if (!conditionView.isChecked) {
                        isEnable = false
                        break
                    }
                } else if (conditionView is TextView) {
                    if (TextUtils.isEmpty(conditionView.text)) {
                        isEnable = false
                        break
                    }
                } else if (conditionView is StatusView) {
                    if (!conditionView.success) {
                        isEnable = false
                        break
                    }
                }
            }
            it.isEnabled = isEnable
        }
    }

    fun recycle() {
        mConditions.clear()
    }

    companion object {
        private const val TAG = "FormHelper"
    }

    interface Input {

        val label: View?

        val input : TextView

        var labelEnable: Boolean
    }

    interface StatusView {

        var success : Boolean

    }
}