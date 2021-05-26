package com.xsw.track.intercept

import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 * ClassName: [OnCreateViewWrapperIntercept]
 * Description:
 *
 * Create by X at 2021/05/26 19:43.
 */
interface OnCreateViewWrapperIntercept {

    fun onInterceptCreateView(
        parent: View?,
        name: String,
        context: Context?,
        attrs: AttributeSet?,
        createdView: View
    )

}
