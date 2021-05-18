package com.xsw.ui.widget.banner.tranforme

import com.xsw.ui.widget.AppCompatViewPager

/**
 * ClassName: [DefaultTransformer]
 * Description:
 *
 * Create by X at 2021/05/18 11:33.
 */
class DefaultTransformer : BaseTransformer() {

    override val orientation: Int
        get() = AppCompatViewPager.OrientationPageTransformer.HORIZONTAL

    override fun minAlpha(): Float {
        return 0.0f
    }

    override fun minScaleX(): Float {
        return 1f
    }

    override fun minScaleY(): Float {
        return 1f
    }

}