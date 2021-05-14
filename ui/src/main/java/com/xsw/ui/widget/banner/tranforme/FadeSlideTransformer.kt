package com.xsw.ui.widget.banner.tranforme

import android.view.View
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs

/**
 * ClassName: [FadeSlideTransformer]
 * Description:
 *
 * Create by X at 2021/05/18 16:18.
 */
class FadeSlideTransformer : ViewPager.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        with(page) {
            translationX = 0f
            if (position <= -1.0f || position >= 1.0f) {
                alpha = 0.0f
            } else if (position == 0.0f) {
                alpha = 1.0f
            } else {
                // position is between -1.0F & 0.0F OR 0.0F & 1.0F
                alpha = 1.0f - abs(position)
            }
        }
    }
}