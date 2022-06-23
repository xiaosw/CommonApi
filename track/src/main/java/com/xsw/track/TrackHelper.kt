package com.xsw.track

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.doudou.log.Logger
import com.xsw.track.intercept.OnClickIntercept
import com.xsw.track.intercept.OnCreateViewWrapperIntercept

/**
 * ClassName: [TrackHelper]
 * Description:
 *
 * Create by X at 2021/05/25 10:13.
 */
object TrackHelper {

    private const val SINGLE_CLICK_DISABLE_DURATION = 1_000
    private var mLastClickTime = 0L

    var onClickIntercept: OnClickIntercept? = null
    var onCreateViewWrapperIntercept: OnCreateViewWrapperIntercept? = null

    @JvmStatic
    fun onClick(view: View?): Boolean {
        return view?.let {
            internalOnClickLocked(it)
        } ?: false
    }

    private inline fun internalOnClickLocked(view: View): Boolean {
        Logger.i("track onClick: $view")
        val clickTime = System.currentTimeMillis()
        if (clickTime - mLastClickTime < SINGLE_CLICK_DISABLE_DURATION) {
            Logger.e("track onClick: 点击间隔太短，拦截！")
            return true
        }
        mLastClickTime = System.currentTimeMillis()
        return onClickIntercept?.onInterceptClick(view) ?: false
    }

    @JvmStatic
    fun onCreateView(
        parent: View?,
        name: String?,
        context: Context?,
        attrs: AttributeSet?,
        createdView: View?
    ) {
        val widgetName = name ?: ""
        createdView?.run {
//            Logger.i("onCreateView: parent = $parent, name = $widgetName, createdView = $createdView")
            onCreateViewWrapperIntercept?.onInterceptCreateView(parent, widgetName, context, attrs, createdView)
        }
    }

}