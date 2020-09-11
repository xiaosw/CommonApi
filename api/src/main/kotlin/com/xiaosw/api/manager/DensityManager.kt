package com.xiaosw.api.manager

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import com.xiaosw.api.annotation.AutoAdjustDensity
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.atomic.AtomicBoolean

private const val TAG = "DensityManager"

/**
 * @ClassName: {@link DensityManager}
 * @Description:
 * 配置优先级规则：
 * @AutoAdjustDensity > addThirdAutoAdjustPage()
 *　　　　
 * Created by admin at 2020-08-27
 * @Email xiaosw0802@163.com
 */

@AutoAdjustDensity
object DensityManager {

    private var mApp: Application? = null

    private val isInitialization = AtomicBoolean(false)

    /**
     * 原始 density
     */
    private var mOriginalDensity = 0F

    /**
     * 原始 ScaledDensity
     */
    private var mOriginalScaledDensity = 0F

    /**
     * 原始 DensityDpi
     */
    private var mOriginalDensityDpi = 0

    private val mAutoAdjustDensityAnnotation by lazy {
        javaClass.getAnnotation(AutoAdjustDensity::class.java) as AutoAdjustDensity
    }

    private val mThirdAutoAdjustPageWhiteList by lazy {
        CopyOnWriteArraySet<Class<out Activity>?>()
    }

    private val mComponentCallbacks by lazy {
        object : ComponentCallbacks {
            override fun onLowMemory() {
            }

            override fun onConfigurationChanged(newConfig: Configuration) {
                mApp?.let {
                    if (newConfig.fontScale > 0) {
                        mOriginalScaledDensity = it.resources.displayMetrics.scaledDensity
                    }
                }
            }

        }
    }

    private val mActivityLifecycleCallbacks by lazy {
        object : Application.ActivityLifecycleCallbacks {

            override fun onActivityCreated(activity: Activity, args: Bundle?) {
                adjustDensityIfNeeded(activity)
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
            }

        }
    }

    @JvmStatic
    @JvmOverloads
    fun init(context: Context) {
        synchronized(this) {
            if (isInitialization.get()) {
                return
            }
            context?.let { ctx ->
                mOriginalDensity = ctx.resources.displayMetrics.density
                mOriginalScaledDensity = ctx.resources.displayMetrics.scaledDensity
                mOriginalDensityDpi = ctx.resources.displayMetrics.densityDpi
                mApp = (ctx.applicationContext as Application).also { app ->
                    app.registerComponentCallbacks(mComponentCallbacks)
                    app.registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks)
                }
            }
        }
    }

    private fun adjustDensityIfNeeded(activity: Activity?) {
        activity?.javaClass?.run {
            var annotation = getAnnotation(AutoAdjustDensity::class.java)
            if (null == annotation && mThirdAutoAdjustPageWhiteList.contains(this)) {
                annotation = mAutoAdjustDensityAnnotation
            }
            annotation?.run {
                modifyDisplayMetrics(mApp, baseDp, baseDpByWidth)
                modifyDisplayMetrics(activity, baseDp, baseDpByWidth)
            }
        }
    }

    private fun modifyDisplayMetrics(context: Context?, baseDp: Float, adjustByWidth: Boolean) {
        context?.let { ctx ->
            ctx.resources?.displayMetrics?.apply {
                val targetDensity = if (baseDp > 0) {
                    ((if (adjustByWidth) widthPixels else heightPixels) / baseDp)
                } else mOriginalDensity

                val targetScaledDensity = targetDensity * (mOriginalScaledDensity / mOriginalDensity)

                val targetDensityDpi = (160 * targetDensity).toInt()

                density = targetDensity
                scaledDensity = targetScaledDensity
                densityDpi = targetDensityDpi
            }
        }
    }

    fun addThirdAutoAdjustPage(clazz: Class<out Activity>) {
        if (mThirdAutoAdjustPageWhiteList.contains(clazz)) {
            return
        }
        mThirdAutoAdjustPageWhiteList.add(clazz)
    }

    fun removeThirdAutoAdjustPage(clazz: Class<out Activity>) {
        if (!mThirdAutoAdjustPageWhiteList.contains(clazz)) {
            return
        }
        mThirdAutoAdjustPageWhiteList.remove(clazz)
    }

    @JvmStatic
    fun release() {
        mApp?.let {
            it.unregisterComponentCallbacks(mComponentCallbacks)
            it.unregisterActivityLifecycleCallbacks(mActivityLifecycleCallbacks)
        }
        mApp = null


        mThirdAutoAdjustPageWhiteList.clear()
    }
}