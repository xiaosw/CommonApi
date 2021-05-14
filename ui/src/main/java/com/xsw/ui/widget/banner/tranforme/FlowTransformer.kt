package com.xsw.ui.widget.banner.tranforme

import android.view.View
import androidx.viewpager.widget.ViewPager

/**
 * ClassName: [FlowTransformer]
 * Description:
 *
 * Create by X at 2021/05/18 16:18.
 */
class FlowTransformer : ViewPager.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.rotationY = position * -30f
    }
}