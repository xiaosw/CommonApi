package com.xsw.ui.widget.banner

import android.view.View
import androidx.annotation.FloatRange

/**
 * ClassName: [Indicator]
 * Description:
 *
 * Create by X at 2021/05/14 10:06.
 */
interface Indicator {

    /**
     * 创建指示器视图
     */
    fun createView() : View

    /**
     * 指示器
     */
    fun onIndicatorScroll(
        @FloatRange(from = 0.0, to = 0.0) xScrollPercent: Float
        , @FloatRange(from = 0.0, to = 0.0) yScrollPercent: Float)

    /**
     * 指示器位置改变
     */
    fun onIndicatorChanged(from: Int, to: Int)

}