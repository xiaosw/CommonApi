package com.xsw.ui.widget.banner.tranforme

import android.view.View
import android.widget.LinearLayout
import com.xsw.ui.widget.AppCompatViewPager
import kotlin.math.abs

/**
 * ClassName: [ZoomOutPageTransformer]
 * Description:
 *
 * Create by X at 2021/05/17 11:40.
 */
class ZoomOutPageTransformer : AppCompatViewPager.OrientationPageTransformer {

    override fun transformPage(page: View, position: Float) {
        with(page) {
            when {
                position < -1 -> { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    alpha = 0f
                }
                position <= 1 -> { // [-1,1]
                    // Modify the default slide transition to
                    // shrink the page as well
                    val scaleFactor = MIN_SCALE.coerceAtLeast(1 - abs(position))
                    val verticalMargin = height * (1 - scaleFactor) / 2
                    val horizontalMargin = width * (1 - scaleFactor) / 2
                    translationX = if (position < 0) {
                        horizontalMargin - verticalMargin / 2
                    } else {
                        -horizontalMargin + verticalMargin / 2
                    }
                    // Scale the page down (between MIN_SCALE and 1)
                    scaleX = scaleFactor
                    scaleY = scaleFactor
                    // Fade the page relative to its size.
                    alpha = (MIN_ALPHA + (scaleFactor - MIN_SCALE)
                            / (1 - MIN_SCALE) * (1 - MIN_ALPHA))
                }
                else -> { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    alpha = 0f
                }
            }
        }
    }

    companion object {
        private const val MIN_SCALE = 0.85f
        private const val MIN_ALPHA = 0.5f
    }

}