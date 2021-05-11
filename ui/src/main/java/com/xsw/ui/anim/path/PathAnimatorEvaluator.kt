package com.xsw.ui.anim.path

import android.animation.TypeEvaluator
import com.xiaosw.api.logger.Logger
import kotlin.math.pow

/**
 * ClassName: [PathAnimatorEvaluator]
 * Description:
 *
 * Create by X at 2021/05/10 18:58.
 */
internal class PathAnimatorEvaluator : TypeEvaluator<PathPointF> {

    private val mEvaluatePoint by lazy {
        PathPointF()
    }

    private val delegate by lazy {
        PathAnimatorEvaluatorDelegateImpl(mEvaluatePoint)
    }

    override fun evaluate(
        fraction: Float,
        startValue: PathPointF?,
        endValue: PathPointF?
    ): PathPointF? {
        return startValue?.let {
            endValue?.let {
                internalEvaluate(fraction, startValue, endValue)
            } ?: null
        } ?: null
    }

    private inline fun internalEvaluate(
        fraction: Float,
        startValue: PathPointF,
        endValue: PathPointF
    ) : PathPointF {
        delegate?.evaluate(fraction, startValue, endValue)?.also {
            return it
        }
        return mEvaluatePoint.also {
            val t: Float = 1f - fraction
            when(endValue.type) {
                PathPointF.Type.LINE -> {
                    it.x = startValue.x + (endValue.x - startValue.x) * fraction
                    it.y = startValue.y + (endValue.y - startValue.y) * fraction
                }

                PathPointF.Type.QUAD -> {
                    it.x = (startValue.x * t.pow(2)).toInt().toFloat()
                            + (2 * endValue.cx1 * fraction * t).toInt()
                            + (endValue.x * fraction.pow(2)).toInt()

                    it.y = (startValue.y * t.pow(2)).toInt().toFloat()
                        + (2 * endValue.cy1 * fraction * t).toInt()
                        + (endValue.y * fraction.pow(2)).toInt()
                }

                PathPointF.Type.CUBIC -> {
                    it.x = startValue.x * t.pow(3)
                        + 3 * endValue.cx1 * fraction * t.pow(2)
                        + 3 * endValue.cx2 * fraction.pow(2) * t
                        + endValue.x * fraction.pow(3)


                    it.y = startValue.y * t.pow(3)
                        + 3 * endValue.cy1 * fraction * t.pow(2)
                        + 3 * endValue.cy2 * fraction.pow(2) * t
                        + endValue.y * fraction.pow(3)
                }

                else -> {
                    it.x = endValue.x
                    it.y = endValue.y
                }
            }
            it.type = endValue.type
        }
    }

}