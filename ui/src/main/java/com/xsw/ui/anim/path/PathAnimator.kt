package com.xsw.ui.anim.path

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.view.View

/**
 * ClassName: [PathAnimator]
 * Description:
 *
 * Create by X at 2021/05/10 14:54.
 */
class PathAnimator(private val targetView: View) : ValueAnimator() {

    private val mPathPointFs by lazy {
        mutableListOf<PathPointF>()
    }

    init {
        addUpdateListener {
            translationTarget(it.animatedValue as? PathPointF)
        }
    }

    override fun start() {
        setObjectValues(*mPathPointFs.toTypedArray())
        super.start()
    }

    override fun setObjectValues(vararg values: Any?) {
        if (getValues() == null || getValues().size === 0) {
            setValues(
                PropertyValuesHolder.ofObject(
                    "translationTarget",
                    PathAnimatorEvaluator(),
                    *values
                )
            )
            return
        }
        super.setObjectValues(*values)
    }

    private fun translationTarget(pathPoint: PathPointF?) {
        pathPoint?.let {
            targetView.translationX = it.x
            targetView.translationY = it.y
        }
    }

    private inline fun initMoveIfNeeded(x: Float = 0f, y: Float = 0f) {
        if (mPathPointFs.isEmpty()) {
            moveTo(x, y)
        }
    }

    fun moveTo(x: Float, y: Float) : PathAnimator {
        addPathPointF(PathPointF.Type.MOVE, x, y)
        return this
    }

    fun rMoveTo(dx: Float, dy: Float) : PathAnimator {
        initMoveIfNeeded(dx, dy)
        with(mPathPointFs.last()) {
            addPathPointF(PathPointF.Type.MOVE, dx + x, dy + y)
        }
        return this
    }

    fun lineTo(x: Float, y: Float) : PathAnimator {
        initMoveIfNeeded()
        addPathPointF(PathPointF.Type.LINE, x, y)
        return this
    }

    fun rLineTo(dx: Float, dy: Float) : PathAnimator {
        initMoveIfNeeded()
        with(mPathPointFs.last()) {
            addPathPointF(PathPointF.Type.LINE, dx + x, dy + y)
        }
        return this
    }

    fun quadTo(x1: Float, y1: Float, x2: Float, y2: Float) : PathAnimator {
        initMoveIfNeeded()
        addPathPointF(PathPointF.Type.QUAD, x1, y1, x2, y2)
        return this
    }

    fun rQuadTo(x1: Float, y1: Float, x2: Float, y2: Float) : PathAnimator {
        if (mPathPointFs.isEmpty()) {
            quadTo(x1, y1, x2, y2)
        }
        with(mPathPointFs.last()) {
            addPathPointF(PathPointF.Type.QUAD, x1 + x, y1 + y, x2 + x, y2 + y)
        }
        return this
    }

    fun cubicTo(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float) : PathAnimator {
        initMoveIfNeeded()
        addPathPointF(PathPointF.Type.CUBIC, x1, y1, x2, y2, x3, y3)
        return this
    }

    fun rCubicTo(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float) : PathAnimator {
        if (mPathPointFs.isEmpty()) {
            cubicTo(x1, y1, x2, y2, x3, y3)
        }
        with(mPathPointFs.last()) {
            addPathPointF(PathPointF.Type.CUBIC, x1 + x, y1 + y, x2 + x
                , y2 + y, x3 + x, y3 + y)
        }
        return this
    }

    private inline fun addPathPointF(
        type: PathPointF.Type,
        x1: Float,
        y1: Float,
        x2: Float = 0f,
        y2: Float = 0f,
        x3: Float = 0f,
        y3: Float = 0f
    ) {
        mPathPointFs.add(PathPointF(type, x1, y1, x2, y2, x3, y3))
    }

}