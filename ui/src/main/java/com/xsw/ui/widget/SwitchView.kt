package com.xsw.ui.widget

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Checkable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.core.content.ContextCompat
import com.xiaosw.api.extend.dp2px
import com.xiaosw.api.extend.save
import com.xiaosw.api.extend.use
import com.xiaosw.api.logger.Logger
import com.xsw.ui.R

/**
 * ClassName: [SwitchView]
 * Description:
 *
 * Create by X at 2021/05/12 10:49.
 */
class SwitchView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr)
    , Checkable
    , ValueAnimator.AnimatorUpdateListener
    , View.OnClickListener {

    private val DEF_MIN_WIDTH by lazy {
        dp2px(78f).toInt()
    }

    private val DEF_MIN_HEIGHT by lazy {
        dp2px(36f).toInt()
    }

    private val mClipWidgetPath by lazy {
        Path()
    }
    private val mTempClipPath by lazy {
        Path()
    }
    private val mWidgetRectF by lazy {
        RectF()
    }
    private val mTempMeasureRectF by lazy {
        RectF()
    }

    private val mDecelerateInterpolator by lazy {
        DecelerateInterpolator()
    }

    private val mChangeCheckedAnim by lazy {
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = switchDuration
            interpolator = mDecelerateInterpolator
            addUpdateListener(this@SwitchView)
        }
    }

    private val mOnTouchUp by lazy {
        object : Runnable {
            var isClick = false

            override fun run() {
                if (isClick) {
                    isClick = false
                    return
                }
                val percent = slidingPercent()
                var checked = percent > 0.5f
                var isInterceptOnClick = isInterceptOnClick()
                if (checked != isChecked && isInterceptOnClick) {
                    checked = isChecked
                }
                Logger.v("mOnTouchUp: checked = $checked")
                onCheckedLocked(checked, false)
                startUpAnim(if (isChecked) percent else 1 - percent, isInterceptOnClick)
            }

        }
    }

    private var mRadius = 0f
    private var mBackground: Drawable? = null
    private var mCheckedBackground: Drawable? = null
    private var mThumb: Drawable? = null
    private var mThumbOffset = 0f
    private var mThumbOffsetHalf = 0

    private var mThumbTop = 0
    private var mThumbBottom = 0
    private var mThumbSize = 0
    private var mSlidingWidth = 0f
    private var mState = State.OFF
    private var mUpAnim: Animator? = null
    private var mCurrentAnim: ValueAnimator? = null
    var interceptOnClick: InterceptOnClick? = null
    var onCheckedChangedListener: OnCheckedChangedListener? = null
    var switchDuration: Long = 0L
        set(value) {
            field = 0L.coerceAtLeast(value)
        }
    var slidingEffectEnable = false
    var slidingEnable = true

    init {
        parseAttrs(context, attrs)
        super.setOnClickListener(this)
    }

    private inline fun parseAttrs(context: Context, attrs: AttributeSet? = null) {
        attrs?.apply {
            context.obtainStyledAttributes(this, R.styleable.SwitchView).use {
                setRadius(getDimension(R.styleable.SwitchView_android_radius, mRadius))
                setThumb(getDrawable(R.styleable.SwitchView_android_thumb))
                setThumbColor(getColor(R.styleable.SwitchView_thumbColor, Color.WHITE))
                setThumbOffset(getDimension(R.styleable.SwitchView_android_thumbOffset, mThumbOffset))
                setCheckedBackgroundColor(getColor(R.styleable.SwitchView_checkedBackgroundColor, Color.parseColor("#37c4ff")))
                onCheckedLocked(getBoolean(R.styleable.SwitchView_android_checked, isChecked), false)
                switchDuration = getInteger(R.styleable.SwitchView_android_duration, 320).toLong()
                slidingEffectEnable = getBoolean(R.styleable.SwitchView_slidingEffectEnable, slidingEffectEnable)
                slidingEnable = getBoolean(R.styleable.SwitchView_slidingEnable, slidingEnable)
            }
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var w = widthMeasureSpec
        var h = heightMeasureSpec
        if (widthMode == MeasureSpec.UNSPECIFIED
            || widthMode == MeasureSpec.AT_MOST
        ) {
            w = MeasureSpec.makeMeasureSpec(minimumWidth, MeasureSpec.EXACTLY)
        }
        if (heightMode == MeasureSpec.UNSPECIFIED
            || heightMode == MeasureSpec.AT_MOST
        ) {
            h = MeasureSpec.makeMeasureSpec(minimumHeight, MeasureSpec.EXACTLY)
        }
        super.onMeasure(w, h)

        val l = paddingLeft
        val t = paddingTop
        val r = measuredWidth - paddingRight
        val b = measuredHeight - paddingBottom
        mWidgetRectF.set(l.toFloat(), t.toFloat(), r.toFloat(), b.toFloat())
        mBackground?.setBounds(l, t, r, b)
        mCheckedBackground?.setBounds(l, t, r, b)

        with(mClipWidgetPath) {
            reset()
            addRoundRect(mWidgetRectF, mRadius, mRadius, Path.Direction.CW)
        }
        mThumbTop = paddingTop + mThumbOffsetHalf
        mThumbBottom = measuredHeight - paddingBottom - mThumbOffsetHalf
        mThumbSize = mThumbBottom - mThumbTop
        mSlidingWidth = measuredWidth - mThumbSize - paddingLeft - paddingRight - mThumbOffset
        onMeasureThumb(if (isChecked) 1f else 0f)
    }

    override fun onDraw(canvas: Canvas?) {
        // super.onDraw(canvas)
        canvas?.run {
            // drawColor(Color.BLUE)
            canvas.clipPath(mClipWidgetPath)
            // background
            if (isChecked) {
                mCheckedBackground?.draw(canvas)
            } else {
                mBackground?.draw(canvas)
            }

            // translate anim
            drawEffect(this)

            // thumb
            drawThumb(this)
        }
    }

    private inline fun drawEffect(canvas: Canvas) {
        val isChecked = isChecked
        if (isScroll && slidingEffectEnable) {
            var percent = slidingPercent()
            drawEffectLocked(canvas, isChecked, if (isChecked) percent else 1 - percent)
            return
        }
        mCurrentAnim?.run {
            if (!slidingEffectEnable && this == mUpAnim) {
                return
            }
            if (!isRunning || animatedValue !is Float) {
                return@run
            }
            val value: Float = animatedValue as Float
            drawEffectLocked(canvas, isChecked, value)
        }
    }

    private fun drawEffectLocked(
        canvas: Canvas,
        isChecked: Boolean,
        @FloatRange(from = 0.0, to = 1.0) value: Float
    ) {
        if (value < 0 || value > 1) {
            return
        }
        mBackground?.bounds?.run {
            canvas.save {
                mTempClipPath.reset()
                val percent = if (isChecked) value else 1 - value
                var l = mThumb?.bounds?.left?.toFloat() ?: 0f
                val leftOffset = paddingLeft + mThumbOffsetHalf.toFloat()
                if (l === leftOffset) {
                    l -= leftOffset
                }
                val hHalf = height() / 2
                mTempMeasureRectF.set(
                    l,
                    top + hHalf * percent,
                    right - height() / 4 * percent,
                    bottom - hHalf * percent
                )
                mTempClipPath.addRoundRect(mTempMeasureRectF, mRadius, mRadius, Path.Direction.CW)
                if (!isChecked) {
                    mCheckedBackground?.draw(this)
                }
                canvas.clipPath(mTempClipPath)
                mBackground?.draw(this)
            }
        }
    }

    private inline fun drawThumb(canvas: Canvas) {
        mThumb?.apply {
            canvas.save {
                mTempClipPath.reset()
                mTempMeasureRectF.set(bounds.left.toFloat()
                    , bounds.top.toFloat()
                    , bounds.right.toFloat()
                    , bounds.bottom.toFloat())
                val thumbRadius =
                    mTempMeasureRectF.centerX().coerceAtMost(mTempMeasureRectF.centerY())
                mTempClipPath.addRoundRect(mTempMeasureRectF
                    , thumbRadius
                    , thumbRadius
                    , Path.Direction.CW)
                clipPath(mTempClipPath)
                draw(this)
            }
        }
    }

    private inline fun slidingPercent() : Float {
        val l = (mThumb?.bounds?.left ?: 0) - paddingLeft - mThumbOffsetHalf
        return 1f.coerceAtMost(l / mSlidingWidth)
    }

    fun setCheckedBackgroundColor(@ColorInt color: Int, invalidate: Boolean = true) {
        if ((mCheckedBackground as? ColorDrawable)?.color == color) {
            return
        }
        setCheckedBackground(ColorDrawable(color), invalidate)
    }

    fun setCheckedBackgroundColorResource(@ColorRes resId: Int, invalidate: Boolean = true) {
        if (resId === NO_ID) {
            return
        }
        setCheckedBackgroundColor(ContextCompat.getColor(context, resId), invalidate)
    }

    fun setCheckedBackgroundColor(color: String, invalidate: Boolean = true) {
        setCheckedBackgroundColor(Color.parseColor(color), invalidate)
    }

    fun setCheckedBackground(drawable: Drawable?, invalidate: Boolean = true) {
        mCheckedBackground = drawable
        checkInvalidate(invalidate)
    }

    override fun setBackground(background: Drawable?) {
        mBackground = background
        invalidate()
    }

    override fun setBackgroundDrawable(background: Drawable?) {
        mBackground = background
        invalidate()
    }

    override fun setChecked(checked: Boolean) {
        onCheckedLocked(checked)
    }

    private inline fun onCheckedLocked(checked: Boolean, enableAnim: Boolean = true) {
        var newState: State? = null
        if (checked && mState != State.ON) {
            newState = State.ON
        } else if (!checked && mState != State.OFF) {
            newState = State.OFF
        }
        newState?.run {
            mState = this
            if (enableAnim) {
                startChangeCheckedAnim()
            }
            onCheckedChangedListener?.onCheckedChanged(this@SwitchView, isChecked)
            invalidate()
        }
    }

    private inline fun onMove(dx: Int) {
        if (!slidingEnable) {
            return
        }
        mThumb?.apply {
            val lMax = measuredWidth - paddingLeft - mThumbOffsetHalf - mThumbSize
            val lMin = paddingLeft + mThumbOffsetHalf
            val l = bounds.left - dx
            if (l < lMin || l > lMax) {
                return
            }
            val r = l + mThumbSize
            setBounds(l, mThumbTop, r, mThumbBottom)
        }
        invalidate()
        isScroll = true
    }

    private var mLastX = 0f
    private var isScroll = false
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (mCurrentAnim?.isRunning == true) {
            Logger.e("onTouchEvent: anim running... ignore!")
            return false
        }
        val result = super.onTouchEvent(event)
        event?.apply {
            val action = action.and(MotionEvent.ACTION_MASK)
            if (action == MotionEvent.ACTION_DOWN) {
                mLastX = x
            } else if (action === MotionEvent.ACTION_MOVE) {
                onMove((mLastX - x).toInt())
                mLastX = x
            } else if (action == MotionEvent.ACTION_UP
                || action == MotionEvent.ACTION_CANCEL) {
                removeCallbacks(mOnTouchUp)
                post(mOnTouchUp)
            }
        }
        return result
    }

    override fun onClick(v: View?) {
        if (isScroll) {
            isScroll = false
            return
        }
        val interceptOnClick = isInterceptOnClick()
        Logger.v("onClick: interceptOnClick = $interceptOnClick")
        if(!interceptOnClick) {
            toggle()
        }
        mOnTouchUp.isClick = true
    }

    override fun getMinimumWidth(): Int {
        val minWidth = super.getMinimumWidth()
        return if (minWidth > 0) minWidth else DEF_MIN_WIDTH
    }

    override fun getMinimumHeight(): Int {
        val minHeight = super.getMinimumHeight()
        return if (minHeight > 0) minHeight else DEF_MIN_HEIGHT
    }

    override fun isChecked(): Boolean {
        return mState == State.ON
    }

    override fun toggle() {
        isChecked = !isChecked
    }

    override fun onAnimationUpdate(animation: ValueAnimator?) {
        mCurrentAnim = animation
        (animation?.animatedValue as? Float)?.let { to ->
            if (isChecked) {
                onSliding(to)
            } else {
                onSliding(0f.coerceAtLeast(1 - to))
            }
            invalidate()
        }
    }

    private inline fun startChangeCheckedAnim() {
        mChangeCheckedAnim.end()
        mChangeCheckedAnim.start()
    }

    private inline fun startUpAnim(@FloatRange(from = 0.0, to = 1.0) scrollPercent: Float
                                   , isInterceptOnClick: Boolean = false) {
        Logger.v("startUpAnim: from = $scrollPercent, interceptOnClick = $isInterceptOnClick")
        if (scrollPercent < 0 || scrollPercent >= 1) {
            return
        }
        mUpAnim?.end()
        mUpAnim = ValueAnimator.ofFloat(scrollPercent, 1f).apply {
            duration = if (isInterceptOnClick) {
                (switchDuration * (1 - scrollPercent)).toLong()
            } else {
                (switchDuration * scrollPercent).toLong()
            }
            interpolator = mDecelerateInterpolator
            addUpdateListener(this@SwitchView)
            start()
        }
    }

    private inline fun isInterceptOnClick() =
        interceptOnClick?.interceptOnClick(this) ?: false

    private inline fun onSliding(percent: Float) {
        onMeasureThumb(percent)
    }

    private inline fun onMeasureThumb(
        @FloatRange(from = 0.0, to = 1.0) percent: Float = if (isChecked) 1f else 0f
    ) {
        if (percent < 0 || percent > 1) {
            return
        }
        mThumb?.apply {
            val l = paddingLeft + mThumbOffsetHalf + mSlidingWidth * percent
            val r = l + mThumbSize
            setBounds(l.toInt(), mThumbTop, r.toInt(), mThumbBottom)
        }
    }

    private inline fun checkInvalidate(invalidate: Boolean) {
        if (invalidate) {
            invalidate()
        }
    }

    @JvmOverloads
    fun setThumb(thumb: Drawable?, invalidate: Boolean = true) {
        if (mThumb == thumb) {
            return
        }
        mThumb = thumb
        checkInvalidate(invalidate)
        onMeasureThumb()
    }

    @JvmOverloads
    fun setRadius(radius: Float, invalidate: Boolean = true) {
        if (radius === mRadius) {
            return
        }
        mRadius = radius
        checkInvalidate(invalidate)
    }

    @JvmOverloads
    fun setThumbResource(@DrawableRes resId: Int, invalidate: Boolean = true) {
        if (resId === NO_ID) {
            return
        }
        setThumb(ContextCompat.getDrawable(context, resId), invalidate)
    }

    @JvmOverloads
    fun setThumbColor(@ColorInt color: Int, invalidate: Boolean = true) {
        if ((mThumb as? ColorDrawable)?.color === color) {
            return
        }
        setThumb(ColorDrawable(color), invalidate)
    }

    @JvmOverloads
    fun setThumbColor(color: String, invalidate: Boolean = true) {
        setThumbColor(Color.parseColor(color), invalidate)
    }

    @JvmOverloads
    fun setThumbOffset(offset: Float, invalidate: Boolean = true) {
        if (mThumbOffset === offset) {
            return
        }
        mThumbOffset = offset
        mThumbOffsetHalf = (mThumbOffset / 2).toInt()
        checkInvalidate(invalidate)
    }

    private enum class State(val desc: String) {
        ON("开启"),
        OFF("关闭")
    }

    interface InterceptOnClick {

        fun interceptOnClick(switchView: SwitchView) : Boolean

    }

    interface OnCheckedChangedListener {

        fun onCheckedChanged(view: SwitchView, isChecked: Boolean)

    }

}