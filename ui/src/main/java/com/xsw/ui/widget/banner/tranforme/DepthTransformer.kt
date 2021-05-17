package com.xsw.ui.widget.banner.tranforme

import android.view.View
import androidx.viewpager.widget.ViewPager

/**
 * ClassName: [DepthTransformer]
 * Description:
 *
 * Create by X at 2021/05/18 16:18.
 */
class DepthTransformer : ViewPager.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        with(page) {
            when {
                position < -1 -> { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    alpha = 0f
                }

                position <= 0 -> { // [-1,0]
                    // Use the default slide transition when
                    // moving to the left page
                    alpha = 1f
                    translationX = 0f
                    scaleX = 1f
                    scaleY = 1f
                }

                position <= 1 -> { // (0,1]
                    // Fade the page out.
                    alpha = 1 - position
                    // Counteract the default slide transition
                    translationX = width * -position
                    // Scale the page down (between MIN_SCALE and 1)
                    val scaleFactor: Float = MIN_SCALE_DEPTH + (1 - MIN_SCALE_DEPTH) * 1 - Math.abs(position)
                    scaleX = scaleFactor
                    scaleY = scaleFactor
                }

                else -> { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    alpha = 0f
                }
            }
        }
    }

    companion object {
        private const val MIN_SCALE_DEPTH = 0.76f
    }
}