package com.xsw.ui.widget.banner.tranforme

import android.view.View
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs

/**
 * ClassName: [ZoomOutSlideTransformer]
 * Description:
 *
 * Create by X at 2021/05/18 16:18.
 */
class ZoomOutSlideTransformer : ViewPager.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        if (position >= -1 || position <= 1) {
            // Modify the default slide transition to shrink the page as well
            with(page) {
                val height = height.toFloat()
                val scaleFactor = MIN_SCALE.coerceAtLeast(1 - abs(position))
                val vertMargin = height * (1 - scaleFactor) / 2
                val horzMargin = width * (1 - scaleFactor) / 2

                // Center vertically
                pivotY = 0.5f * height
                translationX = if (position < 0) {
                    horzMargin - vertMargin / 2
                } else {
                    -horzMargin + vertMargin / 2
                }

                // Scale the page down (between MIN_SCALE and 1)
                scaleX = scaleFactor
                scaleY = scaleFactor

                // Fade the page relative to its size.
                alpha =
                    MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA)
            }
        }
    }

    companion object {
        private const val MIN_SCALE = 0.85f
        private const val MIN_ALPHA = 0.9f
    }
}