package com.xiaosw.api.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.doudou.log.Logger

/**
 * ClassName: [DefActivityLifecycleCallback]
 * Description:
 *
 * Create by X at 2021/04/30 14:15.
 */
interface DefActivityLifecycleCallback : Application.ActivityLifecycleCallbacks {

    override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {
        Logger.v("onActivityPreCreated: $activity")
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Logger.v("onActivityCreated: $activity")
    }

    override fun onActivityPostCreated(activity: Activity, savedInstanceState: Bundle?) {
        Logger.v("onActivityPostCreated: $activity")
    }

    override fun onActivityPreStarted(activity: Activity) {
        Logger.v("onActivityPreStarted: $activity")
    }

    override fun onActivityStarted(activity: Activity) {
        Logger.v("onActivityStarted: $activity")
    }

    override fun onActivityPostStarted(activity: Activity) {
        Logger.v("onActivityPostStarted: $activity")
    }

    override fun onActivityPreResumed(activity: Activity) {
        Logger.v("onActivityPreResumed: $activity")
    }

    override fun onActivityResumed(activity: Activity) {
        Logger.v("onActivityResumed: $activity")
    }

    override fun onActivityPostResumed(activity: Activity) {
        Logger.v("onActivityPostResumed: $activity")
    }

    override fun onActivityPrePaused(activity: Activity) {
        Logger.v("onActivityPrePaused: $activity")
    }

    override fun onActivityPaused(activity: Activity) {
        Logger.v("onActivityPaused: $activity")
    }

    override fun onActivityPostPaused(activity: Activity) {
        Logger.v("onActivityPostPaused: $activity")
    }

    override fun onActivityPreStopped(activity: Activity) {
        Logger.v("onActivityPreStopped: $activity")
    }

    override fun onActivityStopped(activity: Activity) {
        Logger.v("onActivityStopped: $activity")
    }

    override fun onActivityPostStopped(activity: Activity) {
        Logger.v("onActivityPostStopped: $activity")
    }

    override fun onActivityPreSaveInstanceState(activity: Activity, outState: Bundle) {
        Logger.v("onActivityPreSaveInstanceState: $activity")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        Logger.v("onActivitySaveInstanceState: $activity")
    }

    override fun onActivityPostSaveInstanceState(activity: Activity, outState: Bundle) {
        Logger.v("onActivityPostSaveInstanceState: $activity")
    }

    override fun onActivityPreDestroyed(activity: Activity) {
        Logger.v("onActivityPreDestroyed: $activity")
    }

    override fun onActivityDestroyed(activity: Activity) {
        Logger.v("onActivityDestroyed: $activity")
    }

    override fun onActivityPostDestroyed(activity: Activity) {
        Logger.v("onActivityPostDestroyed: $activity")
    }
}