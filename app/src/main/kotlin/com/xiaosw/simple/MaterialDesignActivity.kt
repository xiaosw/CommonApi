package com.xiaosw.simple

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.AppBarLayout
import com.xiaosw.api.logger.Logger
import com.xsw.ui.statusbar.immersionBar
import kotlinx.android.synthetic.main.activity_material_design.*

/**
 * ClassName: [MaterialDesignActivity]
 * Description:
 *
 * Create by X at 2021/05/06 10:28.
 */
open class MaterialDesignActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_material_design)
        resetStatusBarColor()
        app_bar_layout.addOnOffsetChangedListener(object : CompatOnOffsetChangedListener<AppBarLayout>() {

            override fun onOffsetChanged(
                appBarLayout: AppBarLayout,
                verticalOffset: Int,
                status: Status
            ) {
                Logger.e("status = $status, verticalOffset = $verticalOffset")
            }

        })
    }

    ///////////////////////////////////////////////////////////////////////////
    // System Bar
    ///////////////////////////////////////////////////////////////////////////
    protected fun resetStatusBarColor() {
        resetStatusBarColor(getStatusBarColor(), isFitsSystemWindows(), isStatusBarDarkFont())
    }

    protected fun setStatusBarColor(statusBarColor: Int?) {
        setStatusBarColor(statusBarColor, isFitsSystemWindows(), isStatusBarDarkFont())
    }

    @JvmOverloads
    fun resetStatusBarColor(
        currentStatusColor: Int = getStatusBarColor(),
        isFitsSystemWindows: Boolean,
        isStatusBarDarkFont: Boolean
    ) {
        if (!isImmersionBarEnabled()) {
            return
        }
        setStatusBarColor(currentStatusColor, isFitsSystemWindows, isStatusBarDarkFont)
    }

    fun setStatusBarColor(
        statusBarColor: Int?,
        isFitsSystemWindows: Boolean,
        isStatusBarDarkFont: Boolean
    ) {
        if (!isImmersionBarEnabled() || statusBarColor == null) {
            return
        }
        immersionBar {
            fitsSystemWindows(isFitsSystemWindows)
            if (isFitsSystemWindows()) {
                statusBarColorInt(statusBarColor)
            }
            navigationBarColorInt(statusBarColor)
            statusBarDarkFont(isStatusBarDarkFont)
            fullScreen(true)
            navigationBarEnable(false)
            init()
        }
    }

    fun getStatusBarColor(): Int {
        return View.NO_ID
    }

    protected fun isFullScreen(): Boolean {
        return false
    }

    /**
     * 是否使用沉浸式
     */
    protected fun isImmersionBarEnabled(): Boolean {
        return true
    }

    protected fun isStatusBarDarkFont(): Boolean {
        return true
    }

    protected fun isFitsSystemWindows(): Boolean {
        return false
    }

}