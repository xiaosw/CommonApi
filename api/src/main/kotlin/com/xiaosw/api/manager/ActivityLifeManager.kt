package com.xiaosw.api.manager

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import androidx.annotation.Keep
import com.xiaosw.api.AndroidContext
import com.xiaosw.api.extend.isNull
import com.xiaosw.api.extend.showToast
import com.xiaosw.api.logger.Logger
import com.xiaosw.api.util.AppUtils
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.atomic.AtomicBoolean
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

    private val mAppLifecycleListeners by lazy {
        CopyOnWriteArraySet<WeakReference<AppLifecycleListener>>()
    }

    private var mCurrentActivityRef: WeakReference<Activity?>? = null

    private var mLastClickExitTime = 0L

    var topActivity: Activity? = null
        get() = mCurrentActivityRef?.get()

    private var mActiveStartTime = 0L

    private val mIsAppForeground by lazy {
        AtomicBoolean()
    }

    private val isFirstLauncher by lazy {
        AtomicBoolean(true)
    }

    internal fun init(app: Application?) {
        app?.let {
            it.unregisterActivityLifecycleCallbacks(this)
            it.registerActivityLifecycleCallbacks(this)
        }

    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        mActivityList.add(WeakReference(activity))
        Logger.d("onActivityCreated: activity = $activity")
    }

    override fun onActivityStarted(activity: Activity) {
        Logger.d("onActivityStarted: activity = $activity")
    }

    override fun onActivityResumed(activity: Activity) {
        Logger.d("onActivityResumed: activity = $activity, top activity = $topActivity")
        notifyAppForegroundIfNeeded()
        if (mCurrentActivityRef == null
            || mCurrentActivityRef?.get() == null
            || mCurrentActivityRef?.get() != activity) {
            mCurrentActivityRef = WeakReference(activity)
        }
    }

    override fun onActivityPaused(activity: Activity) {
        Logger.d("onActivityPaused: $activity")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        Logger.d("onActivitySaveInstanceState: $activity")
    }

    override fun onActivityStopped(activity: Activity) {
        Logger.d("onActivityStopped: $activity")
        if (mCurrentActivityRef?.get() == activity) {
            return
        }
        notifyAppBackgroundIfNeeded(activity)
    }

    override fun onActivityDestroyed(activity: Activity) {
        Logger.d("onActivityDestroyed: activity = $activity")
        mActivityList.filter {
            val act = it?.get()
            act != null && act != activity && !act.isFinishing
        }.also {
            mActivityList.clear()
            mActivityList.addAll(it)
        }
    }

    fun registerAppLifecycleListener(listener: AppLifecycleListener?) {
        listener?.let { newElement ->
            mAppLifecycleListeners.indexOfFirst { old ->
                newElement == old?.get()
            }.also {
                if (it < 0) {
                    mAppLifecycleListeners.add(WeakReference(newElement))
                }
            }
        }
    }

    fun unregisterAppLifecycleListener(listener: AppLifecycleListener?) {
        mAppLifecycleListeners.forEach {
            if (it?.get() == listener) {
                mAppLifecycleListeners.remove(it)
            }
        }
    }

    fun clearAppLifecycleListener(listener: AppLifecycleListener?) {
        if (mAppLifecycleListeners.isNull(false)) {
            return
        }
        mAppLifecycleListeners?.clear()
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
            if ((this - mLastClickExitTime) > 2000) {
                topActivity?.showToast(prompt)
                mLastClickExitTime = this
            } else {
                exitApp()
            }
        }
    }

    private inline fun notifyAppForegroundIfNeeded() {
        if (mIsAppForeground.get()) {
            return
        }
        mAppLifecycleListeners?.forEach {
            it?.get()?.run {
                onAppForeground(isFirstLauncher.get())
            }
        }
        mActiveStartTime = SystemClock.elapsedRealtime()
        mIsAppForeground.compareAndSet(false, true)
        isFirstLauncher.compareAndSet(true, false)
    }

    private inline fun notifyAppBackgroundIfNeeded(context: Context?) {
        var ctx = context
        if (ctx.isNull()) {
            ctx = AndroidContext.get()
        }
        if (ctx.isNull() || !mIsAppForeground.get()) {
            return
        }
        AppUtils.isAppForeground(ctx, object : AppUtils.CallBack() {
            override fun callBack(isForeground: Boolean) {
               if (!isForeground) {
                   val activeTime = SystemClock.elapsedRealtime() - mActiveStartTime
                   mAppLifecycleListeners?.forEach {
                       it?.get()?.onAppBackground(activeTime)
                   }
                   mActiveStartTime = 0L
                   mIsAppForeground.compareAndSet(true, false)
               }
            }
        })
    }

    /**
     * App 前后台切换监听
     */
    interface AppLifecycleListener {
        /**
         * app 进入前台
         */
        fun onAppForeground(isFirstLauncher: Boolean)

        /**
         * App 进入后台
         * @param activeTime milliseconds
         */
        fun onAppBackground(activeTime: Long)

    }
}