package com.xiaosw.api.netspeed

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import java.lang.Exception

/**
 * ClassName: [NetworkSpeedDelegate]
 * Description:
 *
 * Create by X at 2021/08/24 11:19.
 */
@RequiresApi(Build.VERSION_CODES.M)
internal class NetworkSpeedDelegate(
    private val context: Context,
    private val period: Long = 60_000,
    private val onBucketListener: OnNetworkBucketChangeListener? = null
    ) : Thread(), NetworkSpeed {

    private val nsm by lazy {
        context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
    }

    private val uid by lazy {
        with(context) {
            try {
                packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA)?.applicationInfo?.uid ?: View.NO_ID
            } catch (e: Exception) {
                View.NO_ID
            }
        }
    }

    private var mLastAllBucket: NetworkBucket? = null
    private var mStatus = State.PENDING
    private var mStartTime = -1L
    private var mMaxRx = 0L
    private var mMinRx = 0L
    private var mTotalRx = 0L

    override fun run() {
        super.run()
        try {
            while (isActivity()) {
                internalPeriodQuerySummary()
                sleep(ONE_SECOND_IN_MILLIS)
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    override fun startTrack() {
        if (isActivity()) {
            return
        }
        start()
    }

    override fun stopTrack() {
        mStatus = State.STOP
    }

    override fun onAppForeground(isFirstLauncher: Boolean) {
        if (!isActivity()) {
            return
        }
        mStatus = State.RESUME
    }

    override fun onAppBackground(activeTime: Long) {
        if (!isActivity()) {
            return
        }
        mStatus = State.PAUSE
    }

    override fun isActivity() = (isAlive && mStatus != State.STOP)

    override fun query(type: NetworkType, startTime: Long, endTime: Long) : NetworkBucket {
        return when(type) {
            NetworkType.WIFI -> internalQuerySummaryLocked(type.type, startTime, endTime)
            NetworkType.MOBILE -> internalQuerySummaryLocked(type.type, startTime, endTime)
            else -> {
                internalQueryAllSummaryLocked(startTime, endTime)
            }
        }
    }

    private fun internalPeriodQuerySummary() {
        if (mStatus != State.RESUME) {
            return
        }
        val endTime = System.currentTimeMillis()
        val startTime = endTime - ONE_DAT_IN_MILLIS
        val allBucket = internalQueryAllSummaryLocked(startTime, endTime)
        if (mStartTime <= 0) {
            mStartTime = endTime
        }
        mLastAllBucket?.let {
            val rx = 0L.coerceAtLeast(allBucket.rx - (mLastAllBucket?.rx ?: 0))
//            LG.i(javaClass.simpleName, "querySummary: type = ${allBucket.type}, " +
//                    "rx = ${NetworkSpeedManager.formatSpeed(rx.toFloat())}")
            mTotalRx += rx
            mMaxRx = mMaxRx.coerceAtLeast(rx)
            mMinRx = mMinRx.coerceAtMost(rx)
        }
        if (endTime - mStartTime >= period) {
            onBucketListener?.onNetworkBucketChange(NetworkType.WIFI_AND_MOBILE,mTotalRx * 1000F / period, mMaxRx, mMinRx)
            mStartTime = endTime
            mTotalRx = 0L
            mMaxRx = 0L
            mMinRx = 0L
        }
        mLastAllBucket = allBucket
    }

    private fun internalQueryAllSummaryLocked(startTime: Long, endTime: Long) : NetworkBucket {
        val wifi = internalQuerySummaryLocked(NetworkType.WIFI.type, startTime, endTime)
        val mobile = internalQuerySummaryLocked(NetworkType.MOBILE.type, startTime, endTime)
        var allBucket = NetworkBucket(NetworkType.WIFI_AND_MOBILE.type)
        allBucket.rx = wifi.rx + mobile.rx
        allBucket.tx = wifi.tx + mobile.tx
        return allBucket
    }

    private fun internalQuerySummaryLocked(type: Int, startTime: Long, endTime: Long) : NetworkBucket {
        var resultBucket = NetworkBucket(type)
        if (type != ConnectivityManager.TYPE_WIFI && type != ConnectivityManager.TYPE_MOBILE) {
            return resultBucket
        }
        val summary = nsm.querySummary(type, "", startTime, endTime)
        do {
            var bucket = NetworkStats.Bucket()
            summary.getNextBucket(bucket)
            if (uid === bucket.uid && bucket.endTimeStamp != 0L) {
                resultBucket.tx += bucket.txBytes
                resultBucket.rx += bucket.rxBytes
            }
        } while (summary.hasNextBucket())

        return resultBucket
    }

    private enum class State {
        PENDING,
        RESUME,
        PAUSE,
        STOP,
    }

    companion object {
        const val ONE_SECOND_IN_MILLIS = 1_000L
        const val ONE_DAT_IN_MILLIS = 86_400_000L
    }
}