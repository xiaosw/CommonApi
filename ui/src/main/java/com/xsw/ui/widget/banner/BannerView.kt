package com.xsw.ui.widget.banner

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.xsw.ui.widget.banner.adapter.BannerAdapter

/**
 * ClassName: [BannerView]
 * Description:
 *
 * Create by X at 2021/05/14 09:40.
 */
class BannerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val mViewPager = LoopViewPager(context).also {
        addView(it, LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
            , ViewGroup.LayoutParams.MATCH_PARENT))
    }

    private var mIndicator: Indicator? = null
    private var mIndicatorView: View? = null

    fun setAdapter(adapter: BannerAdapter<*>) {
        mViewPager.adapter = adapter
    }

    @JvmOverloads
    fun <T : Indicator> bindIndicator(
        indicator: T
        , params: LayoutParams = generateDefParams()
    ) {
        mIndicatorView?.let {
            removeView(it)
        }
        mIndicatorView = indicator.createView()
        mIndicatorView?.run {
            addView(this, params)
            mIndicator = indicator
        }
    }

    private inline fun generateDefParams() = LayoutParams(LayoutParams.WRAP_CONTENT
        , LayoutParams.WRAP_CONTENT).also {
            it.addRule(ALIGN_PARENT_BOTTOM.or(CENTER_HORIZONTAL))
    }
}