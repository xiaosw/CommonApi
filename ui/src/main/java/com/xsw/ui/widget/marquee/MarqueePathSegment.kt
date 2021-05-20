package com.xsw.ui.widget.marquee

import android.graphics.Path

/**
 * ClassName: [MarqueePathSegment]
 * Description:
 *
 * Create by X at 2021/05/20 10:05.
 */
class MarqueePathSegment(
    var maxLen: Float = 0f,
    var startD: Float = 0f,
    var stopD: Float = 0f,
    var startWithMoveTo: Boolean = true,
) : Path() {
    private var mInitializerStartD = startD
    private var mInitializerStopD = stopD
    private var mInitializerStartWithMoveTo = startWithMoveTo
    private var mSegmentLen = 0f
    var secondPath: MarqueePathSegment? = null
        private set
    fun init(maxLen: Float,
             startD: Float,
             stopD: Float,
             startWithMoveTo: Boolean = true
    ) {
        this.maxLen = maxLen
        this.startD = startD
        this.stopD = stopD
        this.startWithMoveTo = startWithMoveTo
        mInitializerStartD = startD
        mInitializerStopD = stopD
        mInitializerStartWithMoveTo = startWithMoveTo
        mSegmentLen = stopD - startD
        if (maxLen > 0f) {
            secondPath = MarqueePathSegment()
        }
        super.reset()
    }

    fun computeNextPath(step: Float) {
        secondPath?.let {
            if (stopD >= maxLen) {
                it.stopD = maxLen
                it.startD = startD
                startD = 0f
                stopD = 0f
            } else if (stopD - startD > mSegmentLen) {
                startD += step
                it.startD = 0f
                it.stopD = 0f
            }
            stopD += step
            it.startD += step
        }
    }

    override fun reset() {
        super.reset()
        secondPath?.reset()
    }

    fun reset(onlyResetPath: Boolean = true) = reset(onlyResetPath) {  }

    fun reset(onlyResetPath: Boolean = true, intercept: MarqueePathSegment.() -> Unit) {
        intercept.invoke(this)
        secondPath?.reset(onlyResetPath) {}
        super.reset()
        if (onlyResetPath) {
            return
        }
        startD = mInitializerStartD
        stopD = mInitializerStopD
        startWithMoveTo = mInitializerStartWithMoveTo
    }

}