package com.xsw.ui.widget.marquee.matrix

import android.graphics.Matrix
import android.graphics.Shader

/**
 * ClassName: [MarqueeColorShaderMatrix]
 * Description:
 *
 * Create by X at 2021/05/20 09:57.
 */
class MarqueeColorShaderMatrix(src: Matrix? = null) : Matrix(src) {

    private var mSumDx = 0f
    private var mSumDy = 0f

    fun translate(shader: Shader?, step: Float) {
        shader?.run {
            mSumDx += step
            mSumDy += step
            setTranslate(mSumDx, mSumDy)
            setLocalMatrix(this@MarqueeColorShaderMatrix)
        }
    }

    override fun reset() {
        mSumDx = 0f
        mSumDy = 0f
        super.reset()
    }

}