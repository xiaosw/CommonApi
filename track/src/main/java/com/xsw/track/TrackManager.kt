package com.xsw.track

import android.view.View
import com.xiaosw.api.logger.Logger
import com.xsw.track.intercept.OnClickIntercept

/**
 * ClassName: [TrackManager]
 * Description:
 *
 * Create by X at 2021/05/25 10:13.
 */
object TrackManager {

    private const val SINGLE_CLICK_DISABLE_DURATION = 1_000
    private var mLastClickTime = 0L

    var onClickIntercept: OnClickIntercept? = null

    @JvmStatic
    fun onClick(view: View?) : Boolean {
        return view?.let {
            internalOnClickLocked(it)
        } ?: false
    }

    private inline fun internalOnClickLocked(view: View) : Boolean {
        Logger.e("track onClick: $view")
        val clickTime = System.currentTimeMillis()
        if (clickTime - mLastClickTime < SINGLE_CLICK_DISABLE_DURATION) {
            Logger.e("track onClick: 点击间隔太短，拦截！")
            return true
        }
        mLastClickTime = System.currentTimeMillis()
        return onClickIntercept?.onClick(view) ?: false
    }

}