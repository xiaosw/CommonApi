package com.xsw.ui.widget.banner.tranforme

import android.view.View
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs

/**
 * ClassName: [DepthTransformer]
 * Description:
 *
 * Create by X at 2021/05/18 16:18.
 */
class DepthTransformer : ViewPager.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        val toAlpha: Float
        val toScale: Float
        val toTranslationX: Float
        if (position > 0 && position < 1) {
            // moving to the right
            toAlpha = 1 - position
            toScale = MIN_SCALE_DEPTH + (1 - MIN_SCALE_DEPTH) * (1 - abs(position))
            toTranslationX = page.width * -position
        } else {
            // use default for all other cases
            toAlpha = 1f
            toScale = 1f
            toTranslationX = 0f
        }
        with(page) {
            alpha = toAlpha
            translationX = toTranslationX
            scaleX = toScale
            scaleY = toScale
        }
    }

    companion object {
        private const val MIN_SCALE_DEPTH = 0.76f
    }
}