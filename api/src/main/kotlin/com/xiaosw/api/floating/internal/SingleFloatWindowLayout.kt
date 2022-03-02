package com.xiaosw.api.floating.internal

import android.content.Context
import android.util.AttributeSet
import com.xiaosw.api.AndroidContext
import com.xiaosw.api.floating.FloatWindowController

/**
 * ClassName: [SingleFloatWindowLayout]
 * Description:
 *
 * Create by X at 2022/03/01 16:15.
 */
internal class SingleFloatWindowLayout @JvmOverloads constructor(
    context: Context = AndroidContext.get(), attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FloatWindowLayout(context, attrs, defStyleAttr)
    , FloatWindowController {

    override fun providerTouchDelegate(): FloatWindowLayoutTouchDelegate<FloatWindowLayout> {
        TODO("Not yet implemented")
    }

    override fun addFloatingToWindow(): Boolean {
        
        return true
    }

    override fun onDrag(moveX: Float, moveY: Float): Boolean {
        TODO("Not yet implemented")
    }


}