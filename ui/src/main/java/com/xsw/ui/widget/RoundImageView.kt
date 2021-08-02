package com.xsw.ui.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.Checkable
import androidx.appcompat.widget.AppCompatImageView
import com.xiaosw.api.extend.use
import com.xsw.ui.R

/**
 * ClassName: [AppCompatImageView]
 * Description:
 * forceCircle > radius > topLeftRadius|topRightRadius|bottomRightRadius|bottomLeftRadius
 *
 * Create by X at 2021/05/13 16:50.
 */
class RoundImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr), Checkable {

    private val mDrawRectF by lazy {
        RectF()
    }

    private val mTempRectF by lazy {
        RectF()
    }

    private val mClipPath by lazy {
        Path()
    }

    var radius = 0f
    var topLeftRadius = 0f
    var topRightRadius = 0f
    var bottomRightRadius = 0f
    var bottomLeftRadius = 0f
    var forceCircle = false
    var resizeClip = false
    var roundBackgroundEnable = true
    private var _checked = false

    init {
        parseAttrs(context, attrs)
    }

    private inline fun parseAttrs(context: Context, attrs: AttributeSet? = null) {
        context.obtainStyledAttributes(attrs, R.styleable.RoundImageView).use {
            radius = getDimension(R.styleable.RoundImageView_android_radius, radius)
            forceCircle = getBoolean(R.styleable.RoundImageView_forceCircle, forceCircle)
            resizeClip = getBoolean(R.styleable.RoundImageView_android_resizeClip, resizeClip)
            roundBackgroundEnable = getBoolean(
                R.styleable.RoundImageView_roundBackgroundEnable,
                roundBackgroundEnable
            )
            topLeftRadius = getDimension(
                R.styleable.RoundImageView_android_topLeftRadius,
                topLeftRadius
            )
            topRightRadius = getDimension(
                R.styleable.RoundImageView_android_topRightRadius,
                topRightRadius
            )
            bottomRightRadius = getDimension(
                R.styleable.RoundImageView_android_bottomRightRadius,
                bottomRightRadius
            )
            bottomLeftRadius = getDimension(
                R.styleable.RoundImageView_android_bottomLeftRadius,
                bottomLeftRadius
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mClipPath.reset()
        val r = if (forceCircle) {
            val drawWidth = measuredWidth - paddingLeft - paddingRight
            val drawHeight = measuredHeight - paddingTop - paddingBottom
            val size = drawWidth.coerceAtMost(drawHeight)
            if (resizeClip) {
                setMeasuredDimension(size, size)
                mDrawRectF.set(
                    paddingLeft.toFloat(),
                    paddingTop.toFloat(),
                    measuredWidth - paddingRight.toFloat(),
                    measuredHeight - paddingBottom.toFloat()
                )
            } else {
                val sizeHalf = size / 2
                val cx = drawWidth / 2f
                val cy = drawHeight / 2f
                mDrawRectF.set(
                    cx - sizeHalf, cy - sizeHalf, cx + sizeHalf, cy + sizeHalf
                )
            }

            size / 1f
        } else {
            mDrawRectF.set(
                paddingLeft.toFloat(),
                paddingTop.toFloat(),
                measuredWidth - paddingRight.toFloat(),
                measuredHeight - paddingBottom.toFloat()
            )
            radius
        }
        with(mClipPath) {
            reset()
            val tlr: Float
            val trr: Float
            val brr: Float
            val blr: Float
            if (r > 0) {
                tlr = r
                trr = r
                brr = r
                blr = r
            } else {
                tlr = topLeftRadius
                trr = topRightRadius
                brr = bottomRightRadius
                blr = bottomLeftRadius
            }
            // Logger.e("onMeasure: $size, $forceCircle, $radius, $tlr, $trr, $brr, $blr")
            // top left
            var x = mDrawRectF.left
            var y = mDrawRectF.top
            moveTo(x, y)
            mTempRectF.set(x, y, x + tlr, y + tlr)
            arcTo(mTempRectF, 180f, 90f)

            // top right
            x = mDrawRectF.right - trr
            lineTo(x, y)
            mTempRectF.set(x, y, x + trr, y + trr)
            arcTo(mTempRectF, -90f, 90f)

            // bottom right
            x = mDrawRectF.right
            y = mDrawRectF.bottom - brr
            lineTo(x, y)
            mTempRectF.set(x - brr, y, x, y + brr)
            arcTo(mTempRectF, 0f, 90f)

            // bottom left
            x = mDrawRectF.left + blr
            y = mDrawRectF.bottom
            lineTo(x, y)
            mTempRectF.set(x - blr, y - blr, x, y)
            arcTo(mTempRectF, 90f, 90f)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        if (!roundBackgroundEnable) {
            clipCanvas(canvas)
        }
        super.onDraw(canvas)
    }

    override fun draw(canvas: Canvas?) {
        if (roundBackgroundEnable) {
            clipCanvas(canvas)
        }
        super.draw(canvas)
    }

    private inline fun clipCanvas(canvas: Canvas?) {
        canvas?.run {
            clipPath(mClipPath)
        }
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        val buttonDrawable = drawable
        if (buttonDrawable != null && buttonDrawable.isStateful
            && buttonDrawable.setState(drawableState)
        ) {
            invalidateDrawable(buttonDrawable)
        }
    }

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