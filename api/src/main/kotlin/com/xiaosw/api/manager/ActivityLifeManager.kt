package com.xiaosw.api.manager

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.xiaosw.api.extend.showToast
import com.xiaosw.api.logger.Logger
import java.lang.ref.WeakReference
import kotlin.system.exitProcess

/**
 * @ClassName {@link ActivityLifeManager}
 * @Description
 *
 * @Date 2019-08-22.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
object ActivityLifeManager : Application.ActivityLifecycleCallbacks {

     private val mActivityList by lazy {
        mutableListOf<WeakReference<Activity?>>()
    }
    private var mCurrentActivityRef: WeakReference<Activity?>? = null

    private var mLastClickExitTime = 0L

    var topActivity: Activity? = null
        get() = mCurrentActivityRef?.get()

    internal fun init(app: Application?) {
        app?.let {
            it.unregisterActivityLifecycleCallbacks(this)
            it.registerActivityLifecycleCallbacks(this)
        }

    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        mActivityList.add(WeakReference(activity))
        Logger.d("onActivityCreated: activity = $activity")
    }

    override fun onActivityStarted(activity: Activity?) {
        Logger.d("onActivityStarted: activity = $activity")
    }

    override fun onActivityResumed(activity: Activity?) {
        if (mCurrentActivityRef == null
            || mCurrentActivityRef?.get() == null
            || mCurrentActivityRef?.get() != activity) {
            mCurrentActivityRef = WeakReference(activity)
        }
        Logger.d("onActivityResumed: activity = $activity")
        Logger.d("onActivityResumed: top activity = $topActivity")
    }

    override fun onActivityPaused(activity: Activity?) {
        Logger.d("onActivityPaused: $activity")
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
        Logger.d("onActivitySaveInstanceState: $activity")
    }

    override fun onActivityStopped(activity: Activity?) {
        Logger.d("onActivityStopped: $activity")
    }

    override fun onActivityDestroyed(activity: Activity?) {
        Logger.d("onActivityDestroyed: activity = $activity")
        mActivityList.filter {
            val act = it?.get()
            act != null && act != activity && !act.isFinishing
        }.also {
            mActivityList.clear()
            mActivityList.addAll(it)
        }
    }

    @JvmOverloads
    @JvmStatic
    fun finishAll(ignoreActivity: Activity? = null) {
        mActivityList.forEach {
            it?.get()?.run {
                if (!isFinishing && this != ignoreActivity) {
                    finish()
                }
            }
        }
        mActivityList.clear()
        ignoreActivity?.let {
            mActivityList.add(WeakReference(it))
        }
    }

    @JvmStatic
    fun exitApp() = finishAll().also {
        exitProcess(0)
    }

    @JvmStatic
    fun doubleClickExitApp(prompt: String) {
        with(System.currentTimeMillis()) {
            if ((this - mLastClickExitTime) > 1500) {
                topActivity?.showToast(prompt)
                mLastClickExitTime = this
            } else {
                exitApp()
            }
        }
    }

}