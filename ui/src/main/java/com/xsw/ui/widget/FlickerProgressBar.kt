package com.xsw.ui.widget

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.xiaosw.api.extend.dp2px
import com.xiaosw.api.extend.use
import com.xiaosw.api.logger.Logger
import com.xiaosw.api.util.Utils.hasNull
import com.xsw.ui.R
import kotlin.properties.Delegates

/**
 * ClassName: [FlickerProgressBar]
 * Description:
 *
 * Create by X at 2021/04/20 19:56.
 */
class FlickerProgressBar : View, View.OnClickListener {
    /**
     * 边框颜色
     */
    private var mPauseBorderColor = ContextCompat.getColor(context, R.color._3FB4FF)

    /**
     * 边框宽度
     */
    private var mPauseBorderWidth = context.dp2px(1f)

    /**
     * 等待下载
     */
    private var mDownloadText: String? = Status.DOWNLOAD.desc
    private var mDownloadTextColor = Color.WHITE
    private var mDownloadBackgroundColor = ContextCompat.getColor(context, R.color._3FB4FF)

    /**
     * 正在下载
     */
    private var mDownloadingText: String? = Status.DOWNLOADING.desc
    private var mDownloadingTextColor = ContextCompat.getColor(context, R.color._3FB4FF)
    private var mDownloadingClipTextColor = Color.WHITE
    private var mDownloadingBackgroundColor = Color.GRAY
    private var mDownloadingProgressColor = ContextCompat.getColor(context, R.color._3FB4FF)

    /**
     * 暂停
     */
    private var mPauseText: String? = Status.PAUSE.desc
    private var mPauseTextColor = ContextCompat.getColor(context, R.color._3FB4FF)
    private var mPauseClipTextColor = Color.WHITE
    private var mPauseBackgroundColor = Color.WHITE
    private var mPauseProgressColor = ContextCompat.getColor(context, R.color._3FB4FF)

    /**
     * 下载完
     */
    private var mCompleteText: String? = Status.COMPLETE.desc
    private var mCompleteTextColor = Color.WHITE
    private var mCompleteBackgroundColor = ContextCompat.getColor(context, R.color._3FB4FF)

    /**
     * 已安装
     */
    private var mInstalledText: String? = Status.INSTALLED.desc
    private var mInstalledTextColor = Color.WHITE
    private var mInstalledBackgroundColor = ContextCompat.getColor(context, R.color._3FB4FF)

    /**
     * 文字大小
     */
    private var mTextSize = context.dp2px(13f)

    /**
     * 圆角大小
     */
    private var mRadius = context.dp2px(7f)
    private val mWidgetRectF = RectF()
    private val mBorderRectF = RectF()
    private val mTextRect = Rect()
    private val mProgressRectF = RectF()
    var status: Status = Status.INSTALLED
        set(value) {
            value?.let {
                updateStatus(status, it)
            }
            field = value
            invalidate()
        }
    private var mText: String? = null
    private var mTextPaint: Paint by Delegates.notNull()
    private var mBackgroundPaint: Paint by Delegates.notNull()
    private var mProgressPaint: Paint by Delegates.notNull()
    private var mPauseProgressPaint: Paint by Delegates.notNull()
    private var mMax = 100
    private var mProgress = 50
    private var mProgressPercent = mProgress * HUNDRED_F / mMax
    private val mXfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    var onFlickerProgressBarClickListener: OnFlickerProgressBarClickListener? = null
    private var mOnClickListener: OnClickListener? = null

    @JvmOverloads constructor (
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = NO_ID
    ) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        if (hasNull(context, attrs)) {
            return
        }
        parseAttrs(context, attrs)
        initPaint()
        status = Status.DOWNLOADING
        super.setOnClickListener(this)
    }

    private fun parseAttrs(context: Context, attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.FlickerProgressBar)?.use {
            for (index in 0 until indexCount) {
                when (val attr = getIndex(index)) {
                    R.styleable.FlickerProgressBar_downloadText -> mDownloadText =
                        getString(attr)

                    R.styleable.FlickerProgressBar_downloadingText -> mDownloadingText =
                        getString(attr)

                    R.styleable.FlickerProgressBar_pauseText -> mPauseText = getString(attr)

                    R.styleable.FlickerProgressBar_completeText -> mCompleteText =
                        getString(attr)

                    R.styleable.FlickerProgressBar_installedText -> mInstalledText =
                        getString(attr)
                }
            }
            mDownloadTextColor =
                getColor(R.styleable.FlickerProgressBar_downloadTextColor, mDownloadTextColor)
            mDownloadBackgroundColor =
                getColor(R.styleable.FlickerProgressBar_downloadBackgroundColor, mDownloadBackgroundColor)

            mDownloadingProgressColor =
                getColor(R.styleable.FlickerProgressBar_downloadProgressColor, mDownloadingProgressColor)
            mDownloadingTextColor =
                getColor(R.styleable.FlickerProgressBar_downloadingTextColor, mDownloadingTextColor)
            mDownloadingClipTextColor =
                getColor(R.styleable.FlickerProgressBar_downloadingClipTextColor, mDownloadingClipTextColor)
            mDownloadingBackgroundColor =
                getColor(R.styleable.FlickerProgressBar_downloadingBackgroundColor, mDownloadingBackgroundColor)

            mPauseTextColor =
                getColor(R.styleable.FlickerProgressBar_pauseTextColor, mPauseTextColor)
            mPauseClipTextColor =
                getColor(R.styleable.FlickerProgressBar_pauseClipTextColor, mPauseTextColor)
            mPauseBackgroundColor =
                getColor(R.styleable.FlickerProgressBar_pauseBackgroundColor, mPauseBackgroundColor)
            mPauseProgressColor =
                getColor(R.styleable.FlickerProgressBar_pauseProgressColor, mPauseProgressColor)
            mPauseBorderColor = getColor(R.styleable.FlickerProgressBar_pauseBorderColor, mPauseBorderColor)
            mPauseBorderWidth = getDimension(R.styleable.FlickerProgressBar_pauseBorderWidth, mPauseBorderWidth)

            mCompleteTextColor =
                getColor(R.styleable.FlickerProgressBar_completeTextColor, mCompleteTextColor)
            mCompleteBackgroundColor =
                getColor(R.styleable.FlickerProgressBar_completeBackgroundColor, mCompleteBackgroundColor)

            mInstalledTextColor =
                getColor(R.styleable.FlickerProgressBar_installedTextColor, mInstalledTextColor)

            mInstalledBackgroundColor =
                getColor(R.styleable.FlickerProgressBar_installedBackgroundColor, mInstalledBackgroundColor)


            mTextSize = getDimensionPixelSize(
                R.styleable.FlickerProgressBar_android_textSize,
                mTextSize.toInt()
            ).toFloat()
            mTextSize = getDimensionPixelSize(
                R.styleable.FlickerProgressBar_android_textSize,
                mTextSize.toInt()
            ).toFloat()
            mRadius = getDimension(R.styleable.FlickerProgressBar_android_radius, mRadius)
            mMax = getInt(R.styleable.FlickerProgressBar_android_max, mMax)
            setProgress(getInt(R.styleable.FlickerProgressBar_android_progress, mProgress))
        }

    }

    private fun initPaint() {
        mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).also {
            it.textSize = mTextSize
            it.color = mDownloadTextColor
        }
        mBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).also {
            it.color = mDownloadBackgroundColor
        }
        mProgressPaint = Paint(Paint.ANTI_ALIAS_FLAG).also {
            it.color = mDownloadingProgressColor
        }
        mPauseProgressPaint = Paint(Paint.ANTI_ALIAS_FLAG).also {
            it.color = mPauseTextColor
            it.style = Paint.Style.STROKE
            it.strokeWidth = mPauseBorderWidth
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        mOnClickListener = l
    }

    override fun onClick(v: View) {
        when (status) {
            Status.DOWNLOAD, Status.PAUSE -> {
                status = Status.DOWNLOADING
            }
            Status.DOWNLOADING -> {
                status = Status.PAUSE
            }
        }
        onFlickerProgressBarClickListener?.let {
            it.onClick(v, status)
            return
        }
        mOnClickListener?.let {
            it.onClick(v)
            return
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        var height = 0
        when (heightSpecMode) {
            MeasureSpec.AT_MOST -> height = context.dp2px(DEFAULT_HEIGHT_DP).toInt()
            MeasureSpec.EXACTLY, MeasureSpec.UNSPECIFIED -> height = heightSpecSize
        }
        setMeasuredDimension(widthSpecSize, height)
        mWidgetRectF.set(paddingLeft.toFloat()
            , paddingTop.toFloat()
            , (measuredWidth - paddingRight).toFloat()
            , (measuredHeight - paddingBottom).toFloat())

        val borderOffset = mPauseBorderWidth / 2
        mBorderRectF.set(mWidgetRectF.left + borderOffset
            , mWidgetRectF.top + borderOffset
            , mWidgetRectF.right - borderOffset
            , mWidgetRectF.bottom - borderOffset)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.run {
            drawBackground(this)
            drawProgress(this)
            drawText(this)
            drawClipText(this)
        }
    }

    private fun drawBackground(canvas: Canvas) {
        canvas.drawRoundRect(mWidgetRectF, mRadius, mRadius, mBackgroundPaint)
    }

    private fun drawProgress(canvas: Canvas) {
        val right = mWidgetRectF.right * mProgressPercent / HUNDRED_F
        mProgressRectF.set(mWidgetRectF.left
            , mWidgetRectF.top
            , right
            , mWidgetRectF.bottom)

        val saveCount = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            canvas.saveLayer(mProgressRectF, mProgressPaint)
        } else {
            canvas.saveLayer(mProgressRectF, mProgressPaint, Canvas.ALL_SAVE_FLAG)
        }
        canvas.drawRoundRect(mWidgetRectF, mRadius, mRadius, mProgressPaint)
        val originalXfermode = mProgressPaint.xfermode
        mProgressPaint.xfermode = mXfermode
        canvas.drawRect(mProgressRectF, mProgressPaint)
        mProgressPaint.xfermode = originalXfermode
        canvas.restoreToCount(saveCount)
        if (isPause) {
            canvas.drawRoundRect(
                mBorderRectF, mRadius, mRadius, mPauseProgressPaint
            )
        }
    }

    private fun drawText(canvas: Canvas) {
        mTextPaint.getTextBounds(text, 0, text.length, mTextRect)
        Logger.e("text = $text")
        canvas.drawText(
            text,
            mWidgetRectF.centerX() - mTextRect.width() / 2,
            mWidgetRectF.centerY() + mTextRect.height() / 2,
            mTextPaint
        )
    }

    private fun drawClipText(canvas: Canvas) {
        val originalText = mTextPaint.color
        if (status == Status.DOWNLOADING) {
            mTextPaint.color = mDownloadingClipTextColor
        } else {
            mTextPaint.color = mDownloadingClipTextColor
        }
        mTextPaint.getTextBounds(text, 0, text.length, mTextRect)
        val saveCount = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            canvas.saveLayer(
                mWidgetRectF.left,
                mWidgetRectF.top,
                mWidgetRectF.right * mProgressPercent / HUNDRED_F,
                mWidgetRectF.bottom,
                mTextPaint
            )
        } else {
            canvas.saveLayer(
                mWidgetRectF.left,
                mWidgetRectF.top,
                mWidgetRectF.right * mProgressPercent / HUNDRED_F,
                mWidgetRectF.bottom,
                mTextPaint,
                Canvas.ALL_SAVE_FLAG
            )
        }
        canvas.drawText(
            text,
            mWidgetRectF.centerX() - mTextRect.width() / 2,
            mWidgetRectF.centerY() + mTextRect.height() / 2,
            mTextPaint
        )
        canvas.restoreToCount(saveCount)
        mTextPaint.color = originalText
    }

    private val text: String
        private get() {
            var text = mText
            if (status == Status.DOWNLOADING) {
                text += "$mProgressPercent%"
            }
            return text ?: ""
        }

    private fun updateStatus(from: Status?, to: Status) {
        if (from == to) {
            return
        }
        when (to) {
            Status.DOWNLOAD -> {
                mText = mDownloadText
                mTextPaint.color = mDownloadTextColor
                mBackgroundPaint.color = mDownloadBackgroundColor
            }
            Status.DOWNLOADING -> {
                mText = mDownloadingText
                mBackgroundPaint.color = mDownloadingBackgroundColor
                mTextPaint.color = mDownloadingTextColor
                mProgressPaint.color = mDownloadingProgressColor
            }
            Status.PAUSE -> {
                mText = mPauseText
                mBackgroundPaint.color = mPauseBackgroundColor
                mTextPaint.color = mPauseTextColor
                mProgressPaint.color = mPauseProgressColor
                mPauseProgressPaint.color = mPauseBorderColor
            }

            Status.COMPLETE -> {
                mText = mCompleteText
                mBackgroundPaint.color = mCompleteBackgroundColor
                mTextPaint.color = mCompleteTextColor
            }

            Status.INSTALLED -> {
                mText = mInstalledText
                mTextPaint.color = mInstalledTextColor
                mBackgroundPaint.color = mInstalledBackgroundColor
            }
        }
    }

    fun setMax(max: Int) {
        mMax = max
        setProgress(mProgress)
    }

    @JvmOverloads
    fun setProgress(progress: Int, invalidate: Boolean = true) {
        mProgress = progress.coerceAtMost(mMax)
        mProgressPercent = mProgress * HUNDRED_F / mMax
        if (mProgress >= mMax) {
            status = Status.COMPLETE
        }
        if (invalidate) {
            invalidate()
        }
    }

    val isPause: Boolean
        get() = status == Status.PAUSE

    val isDownloading: Boolean
        get() = status == Status.DOWNLOADING

    interface OnFlickerProgressBarClickListener {
        fun onClick(view: View?, status: Status)
    }

    enum class Status(val desc: String) {
        DOWNLOAD("下载")
        , DOWNLOADING("已下载：")
        , PAUSE("继续")
        , COMPLETE("安装")
        , INSTALLED("打开");
    }

    companion object {
        const val DEFAULT_HEIGHT_DP = 35F
        const val HUNDRED_F = 100F
    }
}