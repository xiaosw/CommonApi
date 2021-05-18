package com.xsw.ui.widget.banner

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.viewpager.widget.ViewPager
import com.xiaosw.api.extend.use
import com.xsw.ui.R
import com.xsw.ui.widget.banner.adapter.BannerAdapter
import com.xsw.ui.widget.listener.OnDataSetChangeListener

/**
 * ClassName: [BannerView]
 * Description:
 *
 * Create by X at 2021/05/14 09:40.
 */
class BannerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr)
    , OnDataSetChangeListener {

    private val mOnPageChangeListener by lazy {
        object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                mBannerIndicator?.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                mBannerIndicator?.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                mBannerIndicator?.onPageScrollStateChanged(state)
            }

        }
    }

    private val mViewPager = LoopViewPager(context).also {
        addView(it, LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
            , ViewGroup.LayoutParams.WRAP_CONTENT))
        it.addOnPageChangeListener(mOnPageChangeListener)
        it.register(this)
    }

    private var mBannerIndicator: BannerIndicator? = null
    private var mBannerIndicatorView: View? = null

    init {
        parseAttrs(context, attrs)
    }

    private fun parseAttrs(context: Context, attrs: AttributeSet? = null) {
        context.obtainStyledAttributes(attrs, R.styleable.BannerView).use {
            setAutoLoop(getBoolean(R.styleable.BannerView_autoLoop, mViewPager.autoLoop))
            setDelayMillis(getInt(R.styleable.BannerView_delayMillis
                , mViewPager.delayMillis.toInt()).toLong())
            setPeriodMillis(getInt(R.styleable.BannerView_periodMillis
                , mViewPager.periodMillis.toInt()).toLong())
            setTouchToScrollEnable(getBoolean(R.styleable.BannerView_touchToScroll
                , mViewPager.scrollEnable))
        }
    }

    fun setAdapter(adapter: BannerAdapter<*>) {
        mViewPager.adapter = adapter
    }

    override fun onDataSetChanged() {
        notifyBannerIndicator()
    }

    @JvmOverloads
    fun <T : BannerIndicator> bindIndicator(
        indicator: T
        , params: LayoutParams = generateDefParams()
    ) {
        mBannerIndicatorView?.let {
            removeView(it)
        }
        mBannerIndicatorView = indicator.createView()
        mBannerIndicatorView?.run {
            addView(this, params)
            mBannerIndicator = indicator
        }
        notifyBannerIndicator()
    }

    @JvmOverloads
    fun setTransform(transformer: ViewPager.PageTransformer, reverseDrawingOrder: Boolean = false) {
        mViewPager.setPageTransformer(reverseDrawingOrder, transformer)
    }

    private inline fun notifyBannerIndicator() {
        (mViewPager.adapter as? BannerAdapter<*>)?.run {
            mBannerIndicator?.onDataSetChanged(this@BannerView, this)
        }
    }

    private inline fun generateDefParams() = LayoutParams(LayoutParams.WRAP_CONTENT
        , LayoutParams.WRAP_CONTENT).also {
            it.addRule(CENTER_HORIZONTAL)
            it.addRule(ALIGN_PARENT_BOTTOM)
    }

    fun setAutoLoop(autoLoop: Boolean) {
        mViewPager.autoLoop = autoLoop
    }

    fun setDelayMillis(delayMillis: Long) {
        mViewPager.delayMillis = delayMillis
    }

    fun setPeriodMillis(periodMillis: Long) {
        mViewPager.periodMillis = periodMillis
    }

    fun setTouchToScrollEnable(enable: Boolean) {
        mViewPager.scrollEnable = enable
    }
}