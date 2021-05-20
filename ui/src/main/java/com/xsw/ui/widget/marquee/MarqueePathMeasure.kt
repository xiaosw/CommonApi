package com.xsw.ui.widget.marquee

import android.graphics.*
import androidx.annotation.FloatRange
import com.xsw.ui.widget.marquee.matrix.MarqueeColorShaderMatrix


/**
 * ClassName: [MarqueePathMeasure]
 * Description:
 *
 * Create by X at 2021/05/20 10:25.
 */
class MarqueePathMeasure(
    private val path: Path = Path()
    , forceClosed: Boolean = true
) : PathMeasure(path, forceClosed) {

    private val mMarqueePaint = Paint(Paint.ANTI_ALIAS_FLAG).also {
        it.style = Paint.Style.STROKE
        it.strokeJoin = Paint.Join.ROUND
        // it.strokeCap = Paint.Cap.ROUND
    }

    private val mShaderColors = intArrayOf(
        Color.parseColor("#FF64A1"),
        Color.parseColor("#A643FF"),
        Color.parseColor("#64EBFF"),
        Color.parseColor("#FFFE39"),
        Color.parseColor("#FF9964")
    )

    private var mDrawRectF = RectF()
    private val mPathRectF = RectF()
    private val mSegmentA = MarqueePathSegment()
    private val mSegmentB = MarqueePathSegment()
    private var mMarqueeColorShader: Shader? = null
    private val mMarqueeColorShaderMatrix = MarqueeColorShaderMatrix()
    var stepPercent: Float = 0f
        set(@FloatRange(from = 0.0, to = 1.0) value) {
            field = value.coerceIn(0f, 1f)
            computeStepLen()
        }

    var maxLen = 0f
        private set(value) {
            field = value
            computeSegmentLen()
            computeStepLen()
        }

    var segmentLen = 0f

    var stepLen = 0f
        private set

    var strokeWidth: Float = 0f
        set(value) {
            field = value
            mMarqueePaint.strokeWidth = value
            computePath()
        }

    var radius: Float = 0f
        set(value) {
            field = value
            computePath()
        }

    private inline fun computeSegmentLen() {
        segmentLen = maxLen / 4f
    }

    private inline fun computeStepLen() {
        stepLen = maxLen * stepPercent
    }

    private inline fun computePath() {
        if (mDrawRectF.width() <= 0) {
            return
        }
        val strokeWidthHalf = strokeWidth / 2f
        if (strokeWidthHalf <= 0) {
            return
        }
        with(path) {
            reset()
            with(mDrawRectF) {
                mPathRectF.set(
                    left + strokeWidthHalf,
                    top + strokeWidthHalf,
                    right - strokeWidthHalf,
                    bottom - strokeWidthHalf
                )
            }
            moveTo(mPathRectF.left, mPathRectF.top)
            addRoundRect(mPathRectF, radius, radius, Path.Direction.CW)
            setPath(this, true)
        }
        var startOffset = 0f
        mSegmentA.init(maxLen, startOffset, startOffset + segmentLen, true)
        startOffset = maxLen / 2
        mSegmentB.init(maxLen, startOffset, startOffset + segmentLen, true,)
    }

    fun onSizeChange(drawRectF: RectF) {
        mDrawRectF = drawRectF
        mMarqueeColorShader = LinearGradient(
            0.0f,
            0.0f,
            0.0f,
            drawRectF.width(),
            mShaderColors,
            floatArrayOf(0.2f, 0.4f, 0.6f, 0.8f, 1.0f),
            Shader.TileMode.REPEAT
        )
        mMarqueePaint.shader = mMarqueeColorShader
        computePath()
    }

    override fun setPath(path: Path?, forceClosed: Boolean) {
        super.setPath(path, forceClosed)
        maxLen = length
    }

    fun drawSegment(
        canvas: Canvas,
    ): Boolean {
        mMarqueeColorShaderMatrix.translate(mMarqueeColorShader, stepLen)
        drawSegment(canvas, mMarqueePaint, mSegmentA)
        drawSegment(canvas, mMarqueePaint, mSegmentB)
        return true
    }

    private inline fun drawSegment(
        canvas: Canvas,
        paint: Paint,
        segment: MarqueePathSegment
    ) : Boolean {
        with(segment) {
            var result = getSegment(startD, stopD, this, startWithMoveTo)
            secondPath?.run {
                getSegment(startD, stopD, this, startWithMoveTo)
            }
            if (stepLen != 0f && maxLen > 0) {
                computeNextPath(stepLen)
                reset(true) {
                    canvas.drawPath(this, paint)
                    secondPath?.let {
                        canvas.drawPath(it, paint)
                    }
                }
            }
            return result
        }
    }

}