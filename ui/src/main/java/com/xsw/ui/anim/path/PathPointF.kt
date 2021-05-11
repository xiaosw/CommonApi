package com.xsw.ui.anim.path

import com.xiaosw.api.logger.Logger

/**
 * ClassName: [PathPointF]
 * Description:
 *
 * Create by X at 2021/05/10 14:56.
 */
internal class PathPointF @JvmOverloads constructor (
    var type: Type? = null
    , x1: Float = 0f
    , y1: Float = 0f
    , x2: Float = 0f
    , y2: Float = 0f
    , x3: Float = 0f
    , y3: Float = 0f
) {

    var x: Float = 0f
    var y: Float = 0f
    var cx1: Float = 0f
    var cy1: Float = 0f
    var cx2: Float = 0f
    var cy2: Float = 0f

    init {
        when(type) {
            Type.QUAD -> {
                cx1 = x1
                cy1 = y1
                x = x2
                y = y2
            }

            Type.CUBIC -> {
                cx1 = x1
                cy1 = y1
                cx2 = x2
                cy2 = y2
                x = x3
                y = y3
            }

            else -> {
                x = x1
                y = y1
            }
        }
    }

    override fun toString(): String {
        return "PathPointF(type=$type, x=$x, y=$y, cx1=$cx1, cy1=$cy1, cx2=$cx2, cy2=$cy2)"
    }

    enum class Type {
        MOVE,
        LINE,
        QUAD,
        CUBIC;
    }

}