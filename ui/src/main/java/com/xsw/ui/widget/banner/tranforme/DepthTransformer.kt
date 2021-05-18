package com.xsw.ui.widget.banner.tranforme

import android.view.View
import androidx.viewpager.widget.ViewPager
import com.xiaosw.api.logger.Logger

/**
 * ClassName: [DepthTransformer]
 * Description:
 *
 * Create by X at 2021/05/18 16:18.
 */
class DepthTransformer : BaseTransformer() {

    override fun minAlpha(): Float {
        return 0f
    }

    override fun minScaleX(): Float {
        return MIN_SCALE_DEPTH
    }

    override fun minScaleY(): Float {
        return MIN_SCALE_DEPTH
    }

    override fun onHorizontalTransformPageIn(
        page: View,
        position: Float,
        alpha: Float,
        scaleX: Float,
        scaleY: Float
    ) {
        super.onHorizontalTransformPageIn(page, position, alpha, scaleX, scaleY)
        with(page) {
            this.alpha = alpha
            translationX = width * -position
        }
    }

    override fun onHorizontalTransformPageOut(
        page: View,
        position: Float,
        alpha: Float,
        scaleX: Float,
        scaleY: Float
    ) {
        super.onHorizontalTransformPageOut(page, position, alpha, scaleX, scaleY)
        with(page) {
            this.alpha = 1f
            translationX = 0f
        }
    }

    companion object {
        private const val MIN_SCALE_DEPTH = 0.76f
    }
}