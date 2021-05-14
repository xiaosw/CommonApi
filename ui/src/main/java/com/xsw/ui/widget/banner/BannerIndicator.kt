package com.xsw.ui.widget.banner

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.xsw.ui.widget.banner.adapter.BannerAdapter

/**
 * ClassName: [BannerIndicator]
 * Description:
 *
 * Create by X at 2021/05/14 10:06.
 */
interface BannerIndicator : ViewPager.OnPageChangeListener {

    fun onDataSetChanged(container: ViewGroup, adapter: BannerAdapter<*>)

    /**
     * 创建指示器视图
     */
    fun createView() : View

}