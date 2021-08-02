package com.xiaosw.api.manager

import android.content.ComponentCallbacks
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import com.xiaosw.api.extend.optionalImpl
import com.xiaosw.api.init.Initializer1Delegate
import com.xiaosw.api.logger.loge
import kotlin.properties.Delegates


/**
 * ClassName: [UIModeManager]
 * Description:
 *
 * Create by X at 2021/08/03 11:23.
 */
object UIModeManager : Initializer1Delegate<Context>(),
    WeakRegisterDelegate.RegisterDelegate<UIModeManager.OnUIModeChangeListener> {
    private var mContext: Context by Delegates.notNull()
    private var mCurrentUiMode: Int = -1
    private val mRegisterDelegate by lazy {
        WeakRegisterDelegate.create<OnUIModeChangeListener>()
    }

    val isSupportDarkMode by lazy {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }

    val isLightMode
        get() = (mContext.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) === Configuration.UI_MODE_NIGHT_NO

    val isDarkMode: Boolean
        get() = !isLightMode

    override fun onInit(context: Context?) = context?.let {
        mContext = it.applicationContext
        mCurrentUiMode = mContext.resources.configuration.uiMode
        if (!isSupportDarkMode) {
            return@let true
        }
        mContext.registerComponentCallbacks(object : ComponentCallbacks by optionalImpl() {
            override fun onConfigurationChanged(newConfig: Configuration) {
                if (mCurrentUiMode != newConfig.uiMode) {
                    val light = isLightMode
                    mRegisterDelegate.forEach { listener ->
                        listener.onUIModeChange(light)
                    }
                    mCurrentUiMode = newConfig.uiMode
                }
            }
        })
        true
    } ?: false

    override fun register(listener: OnUIModeChangeListener) = mRegisterDelegate.register(listener)

    override fun unregister(listener: OnUIModeChangeListener) = mRegisterDelegate.unregister(listener)

    override fun clear() = mRegisterDelegate.clear()

    interface OnUIModeChangeListener {
        fun onUIModeChange(light: Boolean)
    }

}