package com.xsw.ui.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.TextView.BufferType
import com.xiaosw.api.extend.use
import com.xiaosw.api.logger.Logger
import com.xiaosw.api.util.Utils.isEmpty
import com.xsw.ui.R
import kotlin.properties.Delegates

/**
 * ClassName: [FoldTextLayout]
 * Description:
 *
 *
 * Create by X at 2021/04/26 10:02.
 */
class FoldTextLayout @JvmOverloads constructor (
    context: Context
    , attrs: AttributeSet? = null
    , defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    /**
     * 文字
     */
    private var mFoldTextView: FoldTextView by Delegates.notNull()
    /**
     * 操作
     */
    private var mFoldTextActionView: FoldTextActionView by Delegates.notNull()


    init {
        _initView(context)
        _applyStyle(context, attrs)
    }

    private fun _initView(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.layout_fold_text
            , this
            , true)
        mFoldTextActionView = findViewById(R.id.fold_action)
        mFoldTextView = findViewById(R.id.fold_text)
        mFoldTextView.bindFoldTextViewAction(mFoldTextActionView)
    }

    private fun _applyStyle(context: Context, attrs: AttributeSet?) {
        if (isEmpty(context) || isEmpty(attrs)) {
            return
        }
        context.obtainStyledAttributes(attrs, R.styleable.FoldTextLayout)?.use {
            // action
            setFold(getBoolean(R.styleable.FoldTextLayout_isDefFold, mFoldTextView.isFold))
            setFoldText(getString(R.styleable.FoldTextLayout_foldTipText))
            setUnfoldText(getString(R.styleable.FoldTextLayout_unfoldTipText))
            setActionTextSize(
                TypedValue.COMPLEX_UNIT_PX, getDimension(
                    R.styleable.FoldTextLayout_tipTextSize, mFoldTextActionView.getTextSize()
                )
            )
            setActionTextColor(
                getColor(
                    R.styleable.FoldTextLayout_tipTextColor, mFoldTextActionView.getCurrentTextColor()
                )
            )
            setActionTextStyle(getInt(R.styleable.FoldTextLayout_tipTextStyle, Typeface.NORMAL))
            setActionIcon(getDrawable(R.styleable.FoldTextLayout_tipDrawableRight))
            setActionIconAnimEnable(getBoolean(R.styleable.FoldTextLayout_tipDrawableAnimEnable
                , mFoldTextActionView.enableAnim))

            setFoldAnimation(getResourceId(R.styleable.FoldTextLayout_foldAnim, NO_ID))
            setUnfoldAnimation(getResourceId(R.styleable.FoldTextLayout_unfoldAnim, NO_ID))

            // text
            setTextSize(
                TypedValue.COMPLEX_UNIT_PX, getDimension(
                    R.styleable.FoldTextLayout_android_textSize, mFoldTextView.textSize
                )
            )
            setTextColor(
                getColor(
                    R.styleable.FoldTextLayout_android_textColor, mFoldTextView.currentTextColor
                )
            )
            setTextStyle(getInt(R.styleable.FoldTextLayout_android_textStyle, Typeface.NORMAL))
            setMaxLines(
                getInt(
                    R.styleable.FoldTextLayout_android_maxLines,
                    mFoldTextView.maxLines
                )
            )
            setEllipsizeEnd(getText(R.styleable.FoldTextLayout_ellipsizeEnd)?.toString()
                ?: mFoldTextView.ellipsizeEnd)
            setText(getText(R.styleable.FoldTextLayout_android_text))
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // FoldTextView
    ///////////////////////////////////////////////////////////////////////////
    fun setFold(isFold: Boolean) {
        mFoldTextView.isFold = isFold
    }

    fun setEllipsizeEnd(ellipsizeEnd: String?) {
        mFoldTextView.ellipsizeEnd = ellipsizeEnd
    }

    fun setText(resId: Int) = mFoldTextView?.setText(resId)

    fun setText(text: CharSequence?) {
        mFoldTextView.text = text
    }

    fun setText(text: CharSequence?, type: BufferType?) {
        mFoldTextView.setText(text, type)
    }

    fun setText(text: CharArray?, start: Int, len: Int) = mFoldTextView.setText(text, start, len)

    fun setText(resId: Int, type: BufferType?) = mFoldTextView.setText(resId, type)

    fun setTextSize(size: Float) {
        mFoldTextView.textSize = size
    }

    fun setTextSize(unit: Int, size: Float) = mFoldTextView?.setTextSize(unit, size)

    fun setTextColor(color: Int) = mFoldTextView?.setTextColor(color)

    fun setTextStyle(style: Int) = mFoldTextView?.setTypeface(mFoldTextView?.typeface, style)

    fun setMaxLines(maxLines: Int) {
        mFoldTextView?.maxLines = maxLines
    }

    ///////////////////////////////////////////////////////////////////////////
    // FoldTextActionView
    ///////////////////////////////////////////////////////////////////////////

    fun setFoldText(text: CharSequence?) {
        mFoldTextActionView?.foldText = text?.toString()
    }

    fun setUnfoldText(text: CharSequence?) {
        mFoldTextActionView?.unfoldText = text.toString()
    }

    fun setActionTextSize(size: Float) = mFoldTextActionView.setTextSize(size)

    fun setActionTextSize(unit: Int, size: Float) = mFoldTextActionView.setTextSize(unit, size)

    fun setActionTextColor(color: Int) = mFoldTextActionView.setTextColor(color)

    fun setActionTextStyle(style: Int) = mFoldTextActionView.setTextStyle(style)

    fun setActionIcon(drawable: Drawable?) = mFoldTextActionView.setActionIcon(drawable)

    fun setActionIcon(resId: Int) = mFoldTextActionView.setActionIcon(resId)

    fun setActionIcon(bitmap: Bitmap?) = mFoldTextActionView.setActionIcon(bitmap)

    fun setActionIconAnimEnable(enable: Boolean) {
        mFoldTextActionView.enableAnim = enable
    }

    fun setFoldAnimation(animation: Animation) = mFoldTextActionView?.setFoldAnimation(animation)

    fun setFoldAnimation(animationId: Int) = mFoldTextActionView?.setFoldAnimation(animationId)

    fun setUnfoldAnimation(animation: Animation) = mFoldTextActionView?.setUnfoldAnimation(animation)

    fun setUnfoldAnimation(animationId: Int) = mFoldTextActionView?.setUnfoldAnimation(animationId)
}