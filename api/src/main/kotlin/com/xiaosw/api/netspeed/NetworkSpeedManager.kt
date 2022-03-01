package com.xiaosw.api.netspeed

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.xiaosw.api.extend.runOnUiThread
import com.xiaosw.api.init.Initializer1Delegate
import com.xiaosw.api.manager.ActivityLifeManager
import com.xiaosw.api.register.Register
import com.xiaosw.api.register.RegisterDelegate
import java.text.DecimalFormat

/**
 * ClassName: [NetworkSpeedManager]
 * Description:
 *
 * Create by X at 2021/08/24 15:27.
 */
object NetworkSpeedManager : Initializer1Delegate<Application>()
    , Register<OnNetworkBucketChangeListener> {

    private const val MB = 1_048_576.0
    private const val KB = 1_024.0

    private val mAppLifecycleListener  = object : ActivityLifeManager.AppLifecycleListener {
        override fun onAppForeground(isFirstLauncher: Boolean) {
            mNetworkSpeed?.onAppForeground(isFirstLauncher)
        }

        override fun onAppBackground(activeTime: Long) {
            mNetworkSpeed?.onAppBackground(activeTime)
        }

    }

    private val df by lazy {
        DecimalFormat("#.00")
    }

    private val mListeners by lazy {
        RegisterDelegate.createArrayList<OnNetworkBucketChangeListener>()
    }

    private var mNetworkSpeed: NetworkSpeed? = null
    private val mOnBucketChangeListener = object : OnNetworkBucketChangeListener {
        override fun onNetworkBucketChange(
            type: NetworkType,
            avg: Float,
            max: Long,
            min: Long
        ) {
            if (mListeners.isEmpty()) {
                return
            }
            runOnUiThread {
                mListeners.forEach {
                    it?.onNetworkBucketChange(type, avg, max, min)
                }
            }
        }
    }

    override fun onInit(p: Application?): Boolean {
        ActivityLifeManager.register(mAppLifecycleListener)
        return true
    }

    @JvmStatic
    fun startTrack(context: Context, period: Long = 10_000) : Boolean {
        mNetworkSpeed?.let {
            it.startTrack()
            return true
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mNetworkSpeed = NetworkSpeedDelegate(context, period, mOnBucketChangeListener).also {
                it.startTrack()
            }
            return true
        }
        return false
    }

    @JvmStatic
    fun stopTrack() {
        mNetworkSpeed?.apply {
            stopTrack()
            mNetworkSpeed = null
        }
        clear()
    }

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.M)
    fun query(context: Context, type: NetworkType, startTime: Long, endTime: Long) : NetworkBucket {
        return mNetworkSpeed?.query(type, startTime, endTime) ?: NetworkSpeedDelegate(context).query(type, startTime, endTime)
    }

    @JvmStatic
    fun isActivity() = mNetworkSpeed?.isActivity() == true

    @JvmStatic
    fun formatSpeed(speed: Float) : String {
        if (speed >= MB) {
            return "${df.format(speed / MB)}MB/s"
        }
        if (speed >= KB) {
            return "${df.format(speed / KB)}Kb/s"
        }
        return "${speed}B/s"
    }

    override fun register(t: OnNetworkBucketChangeListener) = mListeners.register(t)

    override fun unregister(t: OnNetworkBucketChangeListener) = mListeners.unregister(t)

    override fun clear() = mListeners.clear()

}