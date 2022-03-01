package com.xiaosw.api.netspeed

import android.net.ConnectivityManager

/**
 * ClassName: [NetworkType]
 * Description:
 *
 * Create by X at 2022/03/01 11:34.
 */
enum class NetworkType(val type: Int, val desc: String) {
    WIFI(ConnectivityManager.TYPE_WIFI, "WIFI"),
    MOBILE(ConnectivityManager.TYPE_MOBILE, "Mobile"),
    WIFI_AND_MOBILE(ConnectivityManager.TYPE_WIFI + ConnectivityManager.TYPE_MOBILE, "All")
}