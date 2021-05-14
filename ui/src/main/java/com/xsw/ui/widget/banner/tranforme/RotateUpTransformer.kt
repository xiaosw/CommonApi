package com.xsw.ui.widget.banner.tranforme

import android.view.View
import androidx.viewpager.widget.ViewPager
import com.xsw.ui.widget.banner.BaseBannerIndicator

/**
 * ClassName: [RotateUpTransformer]
 * Description:
 *
 * Create by X at 2021/05/18 16:18.
 */
class RotateUpTransformer : ViewPager.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        with(page) {
            val width = width.toFloat()
            val toRotation = ROT_MOD * position
            pivotX = width * 0.5f
            pivotY = 0f
            translationX = 0f
            rotation = toRotation
        }

    }

    companion object {
        private const val ROT_MOD = -15f
    }
}