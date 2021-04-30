package com.xsw.ui.widget

import android.view.View

/**
 * ClassName: [IFoldTextActionView]
 * Description:
 *
 * Create by X at 2021/04/29 16:53.
 */
interface IFoldTextActionView {

    fun actionWidth() : Int

    fun actionHeight() : Int

    fun setOnClickListener(listener: View.OnClickListener?)

    fun onFoldStateChange(isFold: Boolean)

    fun enableAnim(enable: Boolean)
}