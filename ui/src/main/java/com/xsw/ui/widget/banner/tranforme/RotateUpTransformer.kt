package com.xsw.ui.widget.banner.tranforme

import android.view.View
import androidx.viewpager.widget.ViewPager
import com.xiaosw.api.logger.Logger
import com.xsw.ui.widget.banner.BaseBannerIndicator

/**
 * ClassName: [RotateUpTransformer]
 * Description:
 *
 * Create by X at 2021/05/18 16:18.
 */
class RotateUpTransformer : BaseTransformer() {

    override fun onHorizontalTransformPageIn(
        page: View,
        position: Float,
        alpha: Float,
        scaleX: Float,
        scaleY: Float
    ) {
        with(page) {
            pivotX = width.toFloat() * 0.5f
            pivotY = 0f
            translationX = 0f
            rotation = ROT_MOD * position
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
            pivotY = 0f
            translationX = 0f
            rotation = ROT_MOD * position
        }
    }

    companion object {
        private const val ROT_MOD = -15f
    }
}