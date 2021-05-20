package com.xsw.ui.widget.marquee

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.FloatRange
import com.xiaosw.api.extend.dp2px
import com.xiaosw.api.extend.parseAttrs
import com.xiaosw.api.extend.save
import com.xsw.ui.R

/**
 * ClassName: [MarqueeFrameLayout]
 * Description:
 *
 * Create by X at 2021/05/19 17:16.
 */
class MarqueeFrameLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val mClipPath = Path()
    private val mDrawRectF = RectF()

    private val mMarqueePathMeasure = MarqueePathMeasure()

    private val mRefreshTask = object : Runnable {
        override fun run() {
            removeCallbacks(this)
            invalidate()
            postDelayed(this, INVALIDATE_PERIOD)
        }
    }

    var mode = Mode.BORDER
    var clipBackground = true
    var radius = 0f
        set(value) {
            field = value
            mMarqueePathMeasure.radius = field
        }

    var stepPercent = 0.01f
        set(@FloatRange(from = 0.0, to = 1.0) value) {
            field = value
            mMarqueePathMeasure.stepPercent = field
        }

    var borderWidth = dp2px(6f)
        set(value) {
            field = value
            mMarqueePathMeasure.strokeWidth = value
        }

    init {
        parseAttrs(context, attrs)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        post(mRefreshTask)
    }

    override fun onDetachedFromWindow() {
        removeCallbacks(mRefreshTask)
        super.onDetachedFromWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mDrawRectF.set(
            paddingLeft.toFloat(),
            paddingTop.toFloat(),
            measuredWidth - paddingRight.toFloat(),
            measuredHeight - paddingBottom.toFloat()
        )
        setupClipPath()
        mMarqueePathMeasure.onSizeChange(mDrawRectF)
    }

    override fun draw(canvas: Canvas?) {
        if (clipBackground) {
            canvas?.clipPath(mClipPath)
        }
        super.draw(canvas)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.run {
            if (!clipBackground) {
                clipPath(mClipPath)
            }
        }
        super.onDraw(canvas)
        canvas?.save {
            mMarqueePathMeasure.drawSegment(this)
        }
    }

    private inline fun parseAttrs(context: Context, attrs: AttributeSet? = null) =
        attrs.parseAttrs(context, R.styleable.MarqueeFrameLayout) {
            mode = toMode(getInt(R.styleable.MarqueeFrameLayout_marqueeMode, mode.value))
            radius = getDimension(R.styleable.MarqueeFrameLayout_android_radius, radius)
            borderWidth = getDimension(R.styleable.MarqueeFrameLayout_borderWidth, borderWidth)
            stepPercent = getFloat(R.styleable.MarqueeFrameLayout_stepPercent, stepPercent)
            clipBackground = getBoolean(
                R.styleable.MarqueeFrameLayout_clipBackground,
                clipBackground
            )
        }

    private inline fun setupClipPath() {
        mClipPath.reset()
        mClipPath.moveTo(mDrawRectF.left, mDrawRectF.top)
        mClipPath.addRoundRect(mDrawRectF, radius, radius, Path.Direction.CW)
        mClipPath.close()
    }

    private inline fun toMode(mode: Int) = when(mode) {

        else -> Mode.BORDER
    }

    enum class Mode(val value: Int) {
        BORDER(0)
    }

    companion object {
        private const val INVALIDATE_PERIOD = 16L
    }

}