package com.xsw.ui.widget.banner

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.xiaosw.api.extend.dp2px
import com.xiaosw.api.extend.save
import com.xiaosw.api.extend.use
import com.xiaosw.api.logger.Logger
import com.xsw.ui.R
import com.xsw.ui.widget.banner.adapter.BannerAdapter

/**
 * ClassName: [BaseBannerIndicator]
 * Description:
 *
 * Create by X at 2021/05/14 15:33.
 */
open class BaseBannerIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), BannerIndicator {

    private val mTempMeasureRectF by lazy {
        RectF()
    }

    private val mScrollIndicatorRectF by lazy {
        RectF()
    }

    private val mIndicatorPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).also {
            it.style = Paint.Style.FILL
        }
    }

    private val mIndicatorRectF by lazy {
        mutableListOf<RectF>()
    }

    private var mContainer: ViewGroup? = null
    private var mAdapter: BannerAdapter<*>? = null
    private var mCount = 0
    private var mCurrentItem = 0
    private var mScrollState = ViewPager.SCROLL_STATE_IDLE

    var forceCircle = true
    var radius = context.dp2px(3f)
    var scrollEffect = ScrollEffect.NONE
    var indicatorDividerWidth = context.dp2px(3f)
    var indicatorWidth = context.dp2px(6f)
    var indicatorHeight = context.dp2px(6f)
    var checkColor: Int = ContextCompat.getColor(context, R.color._3FB4FF)

    var uncheckColor: Int = ContextCompat.getColor(context, R.color._F8F8F8)

    init {
        parseAttrs(context, attrs)
    }

    private inline fun parseAttrs(context: Context, attrs: AttributeSet? = null) {
        context.obtainStyledAttributes(attrs, R.styleable.BaseBannerIndicator).use {
            forceCircle = getBoolean(R.styleable.BaseBannerIndicator_forceCycle, forceCircle)
            radius = getDimension(R.styleable.BaseBannerIndicator_android_radius, radius)
            scrollEffect = toEffect(getInt(R.styleable.BaseBannerIndicator_scrollEffect, scrollEffect.value))
            indicatorWidth = getDimension(R.styleable.BaseBannerIndicator_indicatorWidth, indicatorWidth)
            indicatorHeight = getDimension(R.styleable.BaseBannerIndicator_indicatorHeight, indicatorHeight)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = ((mContainer?.paddingLeft ?: 0)
                + paddingLeft
                + indicatorWidth * mCount
                + indicatorDividerWidth * (mCount - 1)
                + paddingRight
                + (mContainer?.paddingRight ?: 0)).toInt()
        val height = ((mContainer?.top ?: 0)
                + paddingTop
                + indicatorHeight
                + paddingBottom
                + (mContainer?.paddingBottom ?: 0)).toInt()
        setMeasuredDimension(width, height)
        mIndicatorRectF.clear()
        var l = paddingLeft + leftPaddingOffset / 1F
        val t = paddingTop + topPaddingOffset / 1F
        var r = l + indicatorWidth
        val b = t + indicatorHeight
        for (index in 0 until mCount) {
            mTempMeasureRectF.set(l, t, r, b)
            if (index === mCurrentItem) {
                mIndicatorPaint.color = checkColor
            } else {
                mIndicatorPaint.color = uncheckColor
            }
            mIndicatorRectF.add(RectF(l, t, r, b))
            l = r + indicatorDividerWidth
            r = l + indicatorWidth
        }

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.run {
            drawIndicator(this)
            drawSlideIndicator(this)
        }
    }

    private inline fun drawSlideIndicator(canvas: Canvas) {
        mIndicatorPaint.color = checkColor
        var r = radius
        if (forceCircle) {
            r = (mScrollIndicatorRectF.width() / 2).coerceAtMost(mScrollIndicatorRectF.height() / 2)
            val cx = mScrollIndicatorRectF.centerX()
            val cy = mScrollIndicatorRectF.centerY()
            mScrollIndicatorRectF.set(cx - r, cy - r, cx + r, cy + r)
        }
        canvas.drawRoundRect(mScrollIndicatorRectF
            , r
            , r
            , mIndicatorPaint)
    }

    private inline fun drawIndicator(canvas: Canvas) {
        mIndicatorRectF?.forEachIndexed { index, item ->
            item?.run {
                mIndicatorPaint.color = if (index === mCurrentItem) checkColor else uncheckColor
                var r = radius
                if (forceCircle) {
                    r = (width() / 2).coerceAtMost(height() / 2)
                    val cx = centerX()
                    val cy = centerY()
                    mTempMeasureRectF.set(cx - r, cy - r, cx + r, cy + r)
                } else {
                    mTempMeasureRectF.set(left, top, right, bottom)
                }
                canvas.drawRoundRect(mTempMeasureRectF
                    , r
                    , r
                    , mIndicatorPaint)
            }
        }
    }

    override fun onDataSetChanged(container: ViewGroup, adapter: BannerAdapter<*>) {
        mContainer = container
        mAdapter = adapter
        mCount = adapter.getRealCount()
    }

    override fun createView(): View {
        return this
    }

    private var mLastPositionOffset = 0f
    private var mLastScrollPosition = 0
    private var toLeft = false
    private var toRight = false
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        scrollEffect(positionOffset, position)
    }

    private inline fun scrollEffect(positionOffset: Float, position: Int) {
        if (scrollEffect === ScrollEffect.NONE) {
            return
        }
        if (mCount <= 1) {
            return
        }
        if (mLastPositionOffset === 0f) {
            mLastPositionOffset = positionOffset
            return
        }
        // positionOffset: from left to right [1, 0], else [0, 1]
        val dist = mLastPositionOffset - positionOffset
        if (mScrollState === ViewPager.SCROLL_STATE_DRAGGING) {
            if (dist > 0) {
                toLeft = true
                toRight = false
            } else if (dist < 0) {
                toLeft = false
                toRight = true
            }
        }
        val isPositionChanged = (mLastScrollPosition != position)
        slidingEffect(isPositionChanged, position, positionOffset)
        mLastPositionOffset = positionOffset
        mLastScrollPosition = position
    }

    private fun slidingEffect(
        isPositionChanged: Boolean,
        position: Int,
        positionOffset: Float
    ) {
        var isEdge = position == mCount - 1
        if (scrollEffect != ScrollEffect.SLIDE) {
            return
        }
        // Logger.e("position = $position, toLeft = $toLeft, toRight = $toRight")
        val l: Float
        val t: Float
        val r: Float
        val b: Float
        if (toLeft && !isPositionChanged) {
            if (isEdge) {
                val next = mIndicatorRectF[mCount - 1]
                val w = next.width()
                t = next.top
                r = next.right
                l = r - w * (1 - positionOffset)
                b = next.bottom
            } else {
                val current = mIndicatorRectF[position + 1]
                val next = mIndicatorRectF[position]
                val w = current.left - next.left
                l = current.left - w * (1 - positionOffset)
                t = current.top
                r = l + current.width()
                b = current.bottom
            }
            mScrollIndicatorRectF.set(l, t, r, b)
            invalidate()
        } else if (toRight && !isPositionChanged) {
            if (isEdge) {
                val next = mIndicatorRectF[0]
                val w = next.width()
                l = next.left
                t = next.top
                r = l + w * positionOffset
                b = next.bottom
            } else {
                val current = mIndicatorRectF[position]
                val next = mIndicatorRectF[position + 1]
                val w = next.left - current.left
                l = current.left + w * positionOffset
                t = current.top
                r = l + current.width()
                b = current.bottom
            }
            mScrollIndicatorRectF.set(l, t, r, b)
            invalidate()
        }
    }

    override fun onPageSelected(position: Int) {
        mCurrentItem = position
        invalidate()
    }

    override fun onPageScrollStateChanged(state: Int) {
        mScrollState = state
        if (state === ViewPager.SCROLL_STATE_IDLE) {
            mScrollIndicatorRectF.set(0f, 0f, 0f, 0f)
            toLeft = false
            toRight = false
            invalidate()
        }
    }

    fun toEffect(value: Int) = when(value) {
        ScrollEffect.SLIDE.value -> ScrollEffect.SLIDE

        else -> ScrollEffect.NONE
    }

    enum class ScrollEffect(val value: Int) {
        NONE(0),
        SLIDE(1);
    }
}