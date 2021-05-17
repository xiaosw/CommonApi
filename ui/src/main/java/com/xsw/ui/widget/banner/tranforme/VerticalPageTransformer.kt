package com.xsw.ui.widget.banner.tranforme

import android.view.View
import android.widget.LinearLayout
import com.xsw.ui.widget.AppCompatViewPager

/**
 * ClassName: [VerticalPageTransformer]
 * Description:
 *
 * Create by X at 2021/05/17 10:04.
 */
class VerticalPageTransformer : AppCompatViewPager.OrientationPageTransformer {

    override val orientation: Int
        get() = AppCompatViewPager.OrientationPageTransformer.VERTICAL

    override fun transformPage(page: View, position: Float) {
        with(page) {
            when {
                position < -1 -> { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    alpha = 0f
                }
                position <= 1 -> { // [-1,1]
                    alpha = 1f
                    // Counteract the default slide transition
                    translationX = width * -position

                    //set Y position to swipe in from top
                    val yPosition = position * height;
                    translationY = yPosition

                }
                else -> { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    alpha = 0f
                }
            }
        }
    }

}