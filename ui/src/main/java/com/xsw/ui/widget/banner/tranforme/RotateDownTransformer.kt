package com.xsw.ui.widget.banner.tranforme

import android.view.View
import androidx.viewpager.widget.ViewPager

/**
 * ClassName: [RotateDownTransformer]
 * Description:
 *
 * Create by X at 2021/05/18 16:18.
 */
class RotateDownTransformer : BaseTransformer() {

    override fun onHorizontalTransformPageIn(
        page: View,
        position: Float,
        alpha: Float,
        scaleX: Float,
        scaleY: Float
    ) {
        with(page) {
            pivotX = width.toFloat() * 0.5f
            pivotY = height.toFloat()
            rotation = ROT_MOD * position * -1.25f
        }
    }

    override fun onHorizontalTransformPageOut(
        page: View,
        position: Float,
        alpha: Float,
        scaleX: Float,
        scaleY: Float
    ) {
        with(page) {
            pivotX = width.toFloat() * 0.5f
            pivotY = height.toFloat()
            rotation = ROT_MOD * position * -1.25f
        }
    }

    companion object {
        private const val ROT_MOD = -15f
    }
}