package com.xsw.track.intercept

import android.view.View

/**
 * ClassName: [OnClickIntercept]
 * Description:
 *
 * Create by X at 2021/05/25 20:44.
 */
interface OnClickIntercept {

    fun onInterceptClick(view: View?) : Boolean

}