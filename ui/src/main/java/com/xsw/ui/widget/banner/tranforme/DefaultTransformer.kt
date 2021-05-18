package com.xsw.ui.widget.banner.tranforme

import android.view.View
import com.xiaosw.api.logger.Logger
import com.xsw.ui.widget.AppCompatViewPager

/**
 * ClassName: [DefaultTransformer]
 * Description:
 *
 * Create by X at 2021/05/18 11:33.
 */
class DefaultTransformer : AppCompatViewPager.OrientationPageTransformer {

    override val orientation: Int
        get() = AppCompatViewPager.OrientationPageTransformer.HORIZONTAL

    override fun transformPage(page: View, position: Float) {
        with(page) {
            when {
                position < -1 -> { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    alpha = 0f
                }

                position < 0 -> { // [-1,0]
                    alpha = 1 + position
                }

                position <= 1 -> { // [0,1]
                    alpha = 1 - position
                }

                else -> { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    alpha = 0f
                }
            }
        }
    }
}