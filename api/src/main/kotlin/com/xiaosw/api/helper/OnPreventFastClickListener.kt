package com.xiaosw.api.helper

import android.view.View
import com.xiaosw.api.config.AppConfig

/**
 * @ClassName [OnPreventFastClickListener]
 * @Description 防快速点击
 *
 * @Date 2019-04-26.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
interface OnPreventFastClickListener : View.OnClickListener {

    var lastClickTime: Long

    override fun onClick(view: View) {
        System.currentTimeMillis().run {
            if (this - lastClickTime < AppConfig.SINGLE_CLICK_GAP_TIME) {
                onFastClick(view)
            } else {
                onSingleClick(view)
            }
            lastClickTime = this
        }
    }

    /**
     * on single click.
     * @see onClick
     */
    fun onSingleClick(view: View)

    /**
     * double click or fast click.
     * @see onClick
     */
    fun onFastClick(view: View){}

}