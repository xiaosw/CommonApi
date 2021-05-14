package com.xsw.ui.widget.banner.tranforme

import android.view.View
import androidx.viewpager.widget.ViewPager

/**
 * ClassName: [RotateDownTransformer]
 * Description:
 *
 * Create by X at 2021/05/18 16:18.
 */
class RotateDownTransformer : ViewPager.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        with(page) {
            val width = width.toFloat()
            val height = height.toFloat()
            val toRotation = ROT_MOD * position * -1.25f
            pivotX = width * 0.5f
            pivotY = height
            rotation = toRotation
        }
    }

    companion object {
        private const val ROT_MOD = -15f
    }
}