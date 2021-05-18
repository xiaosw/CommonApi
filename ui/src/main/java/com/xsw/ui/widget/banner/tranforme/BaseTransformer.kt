package com.xsw.ui.widget.banner.tranforme

import android.view.View
import androidx.annotation.FloatRange
import com.xsw.ui.widget.AppCompatViewPager

/**
 * ClassName: [BaseTransformer]
 * Description:
 *
 * Create by X at 2021/05/18 13:58.
 */
abstract class BaseTransformer : AppCompatViewPager.OrientationPageTransformer {

    final override fun transformPage(page: View, position: Float) {
        val minAlpha = minAlpha().coerceIn(0f, 1f)
        val minScaleX = minScaleX().coerceIn(0f, 1f)
        val minScaleY = minScaleY().coerceIn(0f, 1f)
        when {
            position <= 0 -> {
                transformPageOut(page, position, minAlpha, minScaleX, minScaleY)
            }

            position <= 1 -> {
                transformPageIn(page, position, minAlpha, minScaleX, minScaleY)
            }

            else -> pageHide(page, position, minAlpha, minScaleX, minScaleY)
        }
    }

    private inline fun pageHide(
        page: View
        , position: Float
        , @FloatRange(from = 0.0, to = 1.0) minAlpha: Float
        , @FloatRange(from = 0.0, to = 1.0) minScaleX: Float
        , @FloatRange(from = 0.0, to = 1.0) minScaleY: Float
    ) {
        onPageHide(page, position, minAlpha, minScaleX, minScaleY)
    }

    private inline fun transformPageIn(
        page: View
        , position: Float
        , @FloatRange(from = 0.0, to = 1.0) minAlpha: Float
        , @FloatRange(from = 0.0, to = 1.0) minScaleX: Float
        , @FloatRange(from = 0.0, to = 1.0) minScaleY: Float
    ) {
        onTransformPageIn(page, position, minAlpha, minScaleX, minScaleY)
    }

    private inline fun isVertical() : Boolean {
        return orientation === AppCompatViewPager.OrientationPageTransformer.VERTICAL
    }

    private inline fun transformPageOut(
        page: View
        , position: Float
        , @FloatRange(from = 0.0, to = 1.0) minAlpha: Float
        , @FloatRange(from = 0.0, to = 1.0) minScaleX: Float
        , @FloatRange(from = 0.0, to = 1.0) minScaleY: Float
    ) {
        onTransformPageOut(page, position, minAlpha, minScaleX, minScaleY)
    }

    /**
     * 已隐藏，用户不可见
     * @param position left: [-Infinity, -1) or right: (1, Infinity]
     */
    open fun onPageHide(
        page: View
        , position: Float
        , @FloatRange(from = 0.0, to = 1.0) minAlpha: Float
        , @FloatRange(from = 0.0, to = 1.0) minScaleX: Float
        , @FloatRange(from = 0.0, to = 1.0) minScaleY: Float
    ) {
        with(page) {
            if (minAlpha != 1f) {
                alpha = minAlpha
            }
            if (minScaleX != 1f) {
                scaleX = minScaleX
            }
            if (minScaleY != 1f) {
                scaleY = minScaleY
            }
        }
    }

    /**
     * 准备滑入屏幕
     */
    open fun onTransformPageIn(
        page: View
        , position: Float
        , @FloatRange(from = 0.0, to = 1.0) minAlpha: Float
        , @FloatRange(from = 0.0, to = 1.0) minScaleX: Float
        , @FloatRange(from = 0.0, to = 1.0) minScaleY: Float
    ) {
        with(page) {
            alpha = minAlpha + (1 - minAlpha) * (1 - position)
            scaleX = minScaleX + (1 - minScaleX) * (1 - position)
            scaleY = minScaleY + (1 - minScaleY) * (1 - position)
            if (isVertical()) {
                onVerticalTransformPageIn(page, position, alpha, scaleX, scaleY)
                return
            }
            onHorizontalTransformPageIn(page, position, alpha, scaleX, scaleY)
        }
    }

    /**
     * 准备横向滑入屏幕
     */
    open fun onHorizontalTransformPageIn(
        page: View
        , position: Float
        , @FloatRange(from = 0.0, to = 1.0) alpha: Float
        , @FloatRange(from = 0.0, to = 1.0) scaleX: Float
        , @FloatRange(from = 0.0, to = 1.0) scaleY: Float
    ) {
    }

    /**
     * 准备竖向滑入屏幕
     */
    open fun onVerticalTransformPageIn(
        page: View
        , position: Float
        , @FloatRange(from = 0.0, to = 1.0) alpha: Float
        , @FloatRange(from = 0.0, to = 1.0) scaleX: Float
        , @FloatRange(from = 0.0, to = 1.0) scaleY: Float
    ) {
        with(page) {
            translationX = width * -position
            translationY = height * position
        }
    }

    /**
     * 准备划出屏幕
     */
    open fun onTransformPageOut(
        page: View
        , position: Float
        , @FloatRange(from = 0.0, to = 1.0) minAlpha: Float
        , @FloatRange(from = 0.0, to = 1.0) minScaleX: Float
        , @FloatRange(from = 0.0, to = 1.0) minScaleY: Float
    ) {
        with(page) {
            alpha = 1 - (1 - minAlpha) * -position
            scaleX = 1 - (1 - minScaleX) * -position
            scaleY = 1 - (1 - minScaleY) * -position
            if (isVertical()) {
                onVerticalTransformPageOut(page, position, alpha, scaleX, scaleY)
                return
            }
            onHorizontalTransformPageOut(page, position, alpha, scaleX, scaleY)
        }
    }

    /**
     * 准备横向划出屏幕
     */
    open fun onHorizontalTransformPageOut(
        page: View
        , position: Float
        , @FloatRange(from = 0.0, to = 1.0) alpha: Float
        , @FloatRange(from = 0.0, to = 1.0) scaleX: Float
        , @FloatRange(from = 0.0, to = 1.0) scaleY: Float
    ) {
    }

    /**
     * 准备竖向划出屏幕
     */
    open fun onVerticalTransformPageOut(
        page: View
        , position: Float
        , @FloatRange(from = 0.0, to = 1.0) alpha: Float
        , @FloatRange(from = 0.0, to = 1.0) scaleX: Float
        , @FloatRange(from = 0.0, to = 1.0) scaleY: Float
    ) {
        with(page) {
            translationX = width * -position
            translationY = height * position
        }
    }

    open fun minAlpha() = MIN_ALPHA

    open fun minScaleX() = MIN_SCALE_X

    open fun minScaleY() = MIN_SCALE_Y

    companion object {
        const val MIN_ALPHA = 0.65f
        const val MIN_SCALE_X = 0.65f
        const val MIN_SCALE_Y = 0.65f
    }

}