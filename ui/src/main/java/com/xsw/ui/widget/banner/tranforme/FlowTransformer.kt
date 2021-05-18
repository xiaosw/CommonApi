package com.xsw.ui.widget.banner.tranforme

import android.view.View
import androidx.viewpager.widget.ViewPager

/**
 * ClassName: [FlowTransformer]
 * Description:
 *
 * Create by X at 2021/05/18 16:18.
 */
class FlowTransformer : BaseTransformer() {

    override fun onHorizontalTransformPageIn(
        page: View,
        position: Float,
        alpha: Float,
        scaleX: Float,
        scaleY: Float
    ) {
        page.rotationY = position * -30f
    }

    override fun onHorizontalTransformPageOut(
        page: View,
        position: Float,
        alpha: Float,
        scaleX: Float,
        scaleY: Float
    ) {
        page.rotationY = position * -30f
    }

}