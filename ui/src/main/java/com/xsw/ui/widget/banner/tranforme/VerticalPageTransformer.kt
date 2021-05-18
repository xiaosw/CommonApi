package com.xsw.ui.widget.banner.tranforme

import com.xsw.ui.widget.AppCompatViewPager


/**
 * ClassName: [VerticalPageTransformer]
 * Description:
 *
 * Create by X at 2021/05/17 10:04.
 */
class VerticalPageTransformer : BaseTransformer() {

    override val orientation: Int
        get() = AppCompatViewPager.OrientationPageTransformer.VERTICAL

    override fun minAlpha(): Float {
        return 0.6f
    }

    override fun minScaleX(): Float {
        return 1f
    }

    override fun minScaleY(): Float {
        return 1f
    }

}