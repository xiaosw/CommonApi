package com.xiaosw.api.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Parcelable
import android.telephony.TelephonyManager
import com.xiaosw.api.AndroidContext
import com.xiaosw.api.extend.checkPermissionCompat
import com.xiaosw.api.extend.checkSelfPermissionCompat
import com.xiaosw.api.logger.Logger
import java.lang.ref.WeakReference

/**
 * @ClassName [NetworkUtil]
 * @Description
 *
 * @Date 2019-08-09.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
object NetworkUtil {

    private const val TAG = "NetworkUtil"
    private val mListeners by lazy {
        HashSet<WeakReference<OnNetConnectionChangeListener>>()
    }
    private var mNetStatusReceiver: NetStatusReceiver? = null

    var isConnected: Boolean = false
        private set(value) {
            if(field != value) {
                field = value
                for (listenerRef in mListeners) {
                    listenerRef.get()?.onNetConnectionChange(isConnected)
                }
            }
        }

    fun init() {
        initNetStatus()
        registerNetStatusReceiver()
    }

    @SuppressLint("MissingPermission")
    @Synchronized
    fun initNetStatus() {
        val context = AndroidContext.get()
        // check permission
        if (context.checkSelfPermissionCompat(Manifest.permission.ACCESS_NETWORK_STATE)
            == PackageManager.PERMISSION_GRANTED) {
            val cm = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (cm != null) {
                val info = cm.allNetworkInfo
                if (info != null) {
                    for (i in info.indices) {
                        if (info[i].state == NetworkInfo.State.CONNECTED) {
                            isConnected = true
                            break
                        }
                    }
                }
            }
        }
    }

    /**
     * @param listener
     */
    fun addNetStatusListener(listener: OnNetConnectionChangeListener) = mListeners.add(WeakReference(listener))

    /**
     * @param listener
     * @return
     */
    fun removeNetStatusListener(listener: OnNetConnectionChangeListener) {
        for (listenerRef in mListeners) {
            if (listenerRef.get() == listener) {
                mListeners.remove(listenerRef)
                break
            }
        }
    }

    private fun registerNetStatusReceiver() {
        unregisterNetStatusReceiverIfNeeded()
        mNetStatusReceiver =
            NetStatusReceiver()
        val filter = IntentFilter()
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        AndroidContext.get().registerReceiver(mNetStatusReceiver, IntentFilter(filter))
    }

    private fun unregisterNetStatusReceiverIfNeeded() {
        if (null != mNetStatusReceiver) {
            try {
                AndroidContext.get().unregisterReceiver(mNetStatusReceiver)
            } catch (e: Exception) {
                Logger.e(TAG, "unregsterNetStatusRecevier: ", e)
            }

            mNetStatusReceiver = null
        }
    }

    private class NetStatusReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (WifiManager.WIFI_STATE_CHANGED_ACTION == action) {
                when (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1)) {
                    WifiManager.WIFI_STATE_DISABLED -> isConnected = false
                }
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION == intent.action) {
                val parcelableExtra = intent
                    .getParcelableExtra<Parcelable>(WifiManager.EXTRA_NETWORK_INFO)
                if (null != parcelableExtra) {
                    val networkInfo = parcelableExtra as NetworkInfo
                    val state = networkInfo.state
                    isConnected = state == NetworkInfo.State.CONNECTED// 当然，这边可以更精确的确定状态
                }
            } else if (ConnectivityManager.CONNECTIVITY_ACTION == intent.action) {
                initNetStatus()
            }
        }
    }

    /**
     * Get network type
     *
     * @param context
     * @return
     */
    @JvmStatic
    fun getNetworkType(): String {
        val context = AndroidContext.get()
        if (!context.checkPermissionCompat(Manifest.permission.ACCESS_NETWORK_STATE)) {
            return ""
        }
        var strNetworkType = ""
        val networkInfo =
            (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnected) {
            if (networkInfo.type == ConnectivityManager.TYPE_WIFI) {
                strNetworkType = "wifi"
            } else if (networkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                val _strSubTypeName = networkInfo.subtypeName


                // TD-SCDMA   networkType is 17
                val networkType = networkInfo.subtype
                when (networkType) {
                    TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN //api<8 : replace by 11
                    -> strNetworkType = "2G"
                    TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B //api<9 : replace by 14
                        , TelephonyManager.NETWORK_TYPE_EHRPD  //api<11 : replace by 12
                        , TelephonyManager.NETWORK_TYPE_HSPAP  //api<13 : replace by 15
                    -> strNetworkType = "3G"
                    TelephonyManager.NETWORK_TYPE_LTE    //api<11 : replace by 13
                    -> strNetworkType = "4G"
                    else ->
                        // http://baike.baidu.com/item/TD-SCDMA China Mobile Unicom Telecom Three 3G Standards
                        if (_strSubTypeName.equals("TD-SCDMA", ignoreCase = true) || _strSubTypeName.equals(
                                "WCDMA",
                                ignoreCase = true
                            ) || _strSubTypeName.equals("CDMA2000", ignoreCase = true)
                        ) {
                            strNetworkType = "3G"
                        } else {
                            strNetworkType = _strSubTypeName
                        }
                }

            }
        }
        return strNetworkType
    }

    /**
     * listen network connection status.
     */
    interface OnNetConnectionChangeListener {

        /**
         * @param isConnected
         */
        fun onNetConnectionChange(isConnected: Boolean)

    }

}