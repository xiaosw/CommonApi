package com.xsw.ui.widget.fold

import android.content.Context
import android.text.Layout
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.ViewTreeObserver.OnPreDrawListener
import android.widget.TextView
import com.xiaosw.api.extend.isNull
import com.xiaosw.api.util.StringUtils
import com.xiaosw.api.util.Utils.isEmpty

/**
 * ClassName: [FoldTextView]
 * Description:
 *
 *
 * Create by X at 2021/04/26 10:07.
 */
internal class FoldTextView @JvmOverloads constructor (
    context: Context?
    , attrs: AttributeSet? = null
    , defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr), View.OnClickListener {

    private var mMaxLines = 0
    private var mFoldTextActionView: IFoldTextActionView? = null
    private var mLayout: Layout? = null
    private var isPreDrawComplete = false
    private var mOriginalText: CharSequence? = null
    var ellipsizeEnd: String? = ELLIPSIZE_END
    var isFold = true
        set(value) {
            field = value
            mFoldTextActionView?.onFoldStateChange(value)
        }

    override fun setText(text: CharSequence?, type: BufferType?) {
        mOriginalText = text
        if (StringUtils.isEmpty(text) || mMaxLines <= 0) {
            super.setText(text, type)
            return
        }
        internalPreFormatText(type)
    }

    private fun internalPreFormatText(type: BufferType?) {
        if (isEmpty(mOriginalText)) {
            return
        }
        if (!isPreDrawComplete) {
            viewTreeObserver.addOnPreDrawListener(object : OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    viewTreeObserver.removeOnPreDrawListener(this)
                    isPreDrawComplete = true
                    internalPreFormatText(type)
                    return true
                }
            })
        }
        internalFormatText(type)
    }

    private fun internalFormatText(type: BufferType?) {
        mLayout = layout
        if (mLayout.isNull() || mLayout?.text != mOriginalText) {
            super.setText(mOriginalText, type)
            mLayout = layout
        }

        mLayout?.let {
            internalFormatTextLocked(type)
        } ?: {
            viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    mLayout = layout
                    internalFormatTextLocked(type)
                }
            })
        }
    }

    private fun internalFormatTextLocked(type: BufferType?) {
        if (mOriginalText.isNullOrEmpty()) {
            return
        }
        mLayout?.apply {
            if (lineCount <= mMaxLines) {
                super.setText(mOriginalText, type)
                return
            }
            val targetLineIndex = if (isFold) {
                mMaxLines
            } else {
                lineCount
            }
            val start = getLineStart(targetLineIndex - 1)
            val end = getLineVisibleEnd(targetLineIndex - 1)
            val lastLineText = mOriginalText?.subSequence(start, end)?.toString() ?: ""
            val lastLineLen = lastLineText.length
            val maxWidth = measuredWidth - paddingLeft - paddingRight - actionWidth()
            val targetText: String
            val paint = paint
            targetText = when {
                isFold -> {
                    val ellipsizeWidth = ellipsizeEnd?.let {
                        paint.measureText(it)
                    } ?: 0f
                    var trimLen = 0
                    while (ellipsizeWidth
                        + paint.measureText(lastLineText.substring(0, lastLineLen - trimLen)) > maxWidth) {
                        trimLen++
                    }
                    mOriginalText?.subSequence(0, end - trimLen).toString() + ellipsizeEnd
                }

                actionWidth() + paint.measureText(lastLineText) > maxWidth -> {
                    "$mOriginalText\n"
                }

                else -> {
                    mOriginalText.toString() + ""
                }
            }
            super.setText(targetText, type)
        }
    }

    override fun onClick(v: View?) {
        toggle()
    }

    override fun getMaxLines(): Int {
        return mMaxLines
    }

    override fun setMaxLines(maxLines: Int) {
        mMaxLines = maxLines
    }

    fun bindFoldTextViewAction(action: IFoldTextActionView?) {
        mFoldTextActionView = action?.also {
            it.setOnClickListener(this)
        }
    }

    private fun actionWidth(): Int {
        return mFoldTextActionView?.actionWidth() ?: 0
    }

    fun toggle() {
        isFold = !isFold
        text = mOriginalText
    }

    companion object {
        private const val ELLIPSIZE_END = "..."
    }
}