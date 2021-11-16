package com.xiaosw.api.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.TelephonyManager
import android.text.format.Formatter
import com.doudou.log.Logger
import com.xiaosw.api.AndroidContext
import com.xiaosw.api.extend.checkSelfPermissionCompat
import com.xiaosw.api.extend.tryCatch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Inet6Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*

/**
 * @ClassName [DeviceUtil]
 * @Description
 *
 * @Date 2019-08-23.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
object DeviceUtil {

    private const val TAG = "DeviceUtil"

    /**
     * Get cell phone information
     *
     * @param type
     * @return
     * @throws PackageManager.NameNotFoundException
     */
    @SuppressLint("MissingPermission")
    fun getPhoneInfo(type: Int): String {
        try {
            val context = AndroidContext.get()
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            when (type) {
                1// Device unique identifier
                ->
                    return ""

                2// System version number
                -> return Build.VERSION.RELEASE

                3// Equipment model
                -> return Build.MODEL

                4// Application version number
                -> return context.packageManager.getPackageInfo(context.packageName, 0
                ).versionName

                5// Mobile phone brands
                -> return Build.BRAND

                6//IMSI
                -> {
                    if (context.checkSelfPermissionCompat(Manifest.permission.READ_PHONE_STATE)) {
                        return telephonyManager.subscriberId
                    } else {
                        Logger.e("getPhoneInfo: not granted READ_PHONE_STATE permission!",
                            TAG
                        )
                    }
                    return ""
                }
                7// Operating system version
                -> {
                    if (context.checkSelfPermissionCompat(Manifest.permission.READ_PHONE_STATE)) {
                        return telephonyManager.deviceSoftwareVersion + ""
                    } else {
                        Logger.e("getPhoneInfo: not granted READ_PHONE_STATE permission!",
                            TAG
                        )
                    }
                    return ""
                }
                8//SIM card serial number
                -> {
                    if (context.checkSelfPermissionCompat(Manifest.permission.READ_PHONE_STATE)) {
                        return telephonyManager.simSerialNumber
                    } else {
                        Logger.e("getPhoneInfo: not granted READ_PHONE_STATE permission!",
                            TAG
                        )
                    }
                    return ""
                }

                9//Cup model
                -> return Build.HARDWARE

                10//Storage size
                -> return Formatter.formatFileSize(context, FileSizeUtil.getTotalInternalMemorySize())

                11//Storage size available
                -> return Formatter.formatFileSize(context, FileSizeUtil.getAvailableInternalMemorySize())

                12// RAM
                -> return Formatter.formatFileSize(context, FileSizeUtil.getTotalMemorySize(context))

                13// Remaining memory
                -> return Formatter.formatFileSize(context, FileSizeUtil.getAvailableMemory(context))

                14//MAC address
                -> return getMacAddress()

                else -> return ""
            }
        } catch (e: Exception) {
            Logger.e("getPhoneInfo: Abnormal.",
                TAG, e)
        }

        return ""
    }

    /**
     * Get ip address
     *
     * @return
     */
    fun getHostIP(): String? {
        val context = AndroidContext.get()
        var hostIp: String? = null
        try {
            if (!context.checkSelfPermissionCompat(Manifest.permission.ACCESS_NETWORK_STATE)) {
                return ""
            }
            val info = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
            if (info == null || !info.isConnected) {
                return null
            }
            if (info.type == ConnectivityManager.TYPE_MOBILE) { // 10ms
                val nis = NetworkInterface.getNetworkInterfaces()
                var ia: InetAddress
                while (nis.hasMoreElements()) {
                    val ni = nis.nextElement() as NetworkInterface
                    val ias = ni.inetAddresses
                    while (ias.hasMoreElements()) {
                        ia = ias.nextElement()
                        if (ia is Inet6Address) {
                            continue// skip ipv6
                        }
                        val ip = ia.hostAddress
                        if ("127.0.0.1" != ip) {
                            hostIp = ia.hostAddress
                            break
                        }
                    }
                }
            } else if (info.type == ConnectivityManager.TYPE_WIFI) {//Currently using a wireless network
                // 1ms
                if (!context.checkSelfPermissionCompat(Manifest.permission.ACCESS_WIFI_STATE)) {
                    return ""
                }
                tryCatch(def = null) {
                    val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                    if (null != wifiManager) {
                        val wifiInfo = wifiManager.connectionInfo
                        if (null != wifiInfo) {
                            hostIp =
                                intToIpAddress(wifiInfo.ipAddress)//Get IPV4 address
                        }
                    }
                }
                Logger.e("getHostIP: $hostIp",
                    TAG
                )
            }
        } catch (e: SocketException) {
            Logger.e("getHostIP", TAG, e)
        }

        return hostIp
    }

    /**
     * Get your phone's Mac address and get your phone's Mac address if WiFi is not turned on or connected
     */
    fun getMacAddress(context: Context): String? {
        var macAddress: String? = null
        val wifiInfo = getWifiInfo(context)
        if (wifiInfo != null) {
            macAddress = wifiInfo.macAddress
        }
        return macAddress
    }

    /**
     * Get the phone's Ip address
     */
    fun getIpAddress(context: Context): String? {
        var ipAddress: String? = null
        val wifiInfo = getWifiInfo(context)
        if (wifiInfo != null) {
            ipAddress = intToIpAddress(wifiInfo.ipAddress)
        }
        return ipAddress
    }

    /**
     * get WifiInfo
     */
    fun getWifiInfo(context: Context): WifiInfo? {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        var info: WifiInfo? = null
        if (null != wifiManager) {
            info = wifiManager.connectionInfo
        }
        return info
    }


    fun ipAddressToInt(ip: String): Long {
        val items = ip.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return (java.lang.Long.valueOf(items[0]) shl 24
                or (java.lang.Long.valueOf(items[1]) shl 16)
                or (java.lang.Long.valueOf(items[2]) shl 8)
                or java.lang.Long.valueOf(items[3]))
    }

    fun intToIpAddress(ipInt: Int): String {
        val sb = StringBuffer()
        sb.append(ipInt and 0xFF).append(".")
        sb.append(ipInt shr 8 and 0xFF).append(".")
        sb.append(ipInt shr 16 and 0xFF).append(".")
        sb.append(ipInt shr 24 and 0xFF)
        return sb.toString()
    }

    /**
     * Get current connectable Wifi list
     */
    fun getAvailableNetworks(context: Context): List<*>? {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        var wifiList: List<ScanResult>? = null
        if (wifiManager != null) {
            wifiList = wifiManager.scanResults
        }
        return wifiList
    }

    /**
     * Get the MAC address of the connected Wifi router
     */
    fun getConnectedWifiMacAddress(context: Context): String? {
        var connectedWifiMacAddress: String? = null
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiList: List<ScanResult>?

        if (wifiManager != null) {
            wifiList = wifiManager.scanResults
            val info = wifiManager.connectionInfo
            if (wifiList != null && info != null) {
                for (i in wifiList.indices) {
                    val result = wifiList[i]
                    if (info.bssid == result.BSSID) {
                        connectedWifiMacAddress = result.BSSID
                    }
                }
            }
        }
        return connectedWifiMacAddress
    }

    /**
     * Get Mac address
     */
    @JvmStatic
    fun getMacAddress(): String {
        try {
            val all = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (nif in all) {
                if (!nif.name.equals("wlan0", ignoreCase = true)) continue

                val macBytes = nif.hardwareAddress ?: return ""

                val res1 = StringBuilder()
                for (b in macBytes) {
                    res1.append(String.format("%02X:", b))
                }

                if (res1?.length > 0) {
                    res1.deleteCharAt(res1.length - 1)
                }
                return res1.toString()
            }
        } catch (ex: Exception) {
            Logger.e("getMacAddress", TAG, ex)
        }

        return "02:00:00:00:00:00"
    }

    /**
     * Get system language
     *
     * @return
     */
    fun getLanguage(): String {
        return Locale.getDefault().language

    }

    /**
     * Get current country
     *
     * @return
     */
    fun getCountry(): String {
        return Locale.getDefault().country

    }


    /**
     * get DNS
     *
     * @return
     */
    fun getLocalDNS(): String? {
        var cmdProcess: Process? = null
        var reader: BufferedReader? = null
        var dnsIP = ""
        return try {
            cmdProcess = Runtime.getRuntime().exec("getprop net.dns1")
            reader = BufferedReader(InputStreamReader(cmdProcess!!.inputStream))
            dnsIP = reader.readLine()
            dnsIP
        } catch (e: Exception) {
            null
        } finally {
            try {
                reader?.close()
            } catch (e: Exception) {
            }

            cmdProcess?.destroy()
        }
    }

    /**
     * Channel name
     */
    fun getAppChannel(context: Context): String {
        var appChannel: String? = ""
        var appInfo: ApplicationInfo? = null
        tryCatch {
            appInfo = context.packageManager
                .getApplicationInfo(
                    context.packageName,
                    PackageManager.GET_META_DATA
                )
            appChannel = appInfo!!.metaData.getString("UMENG_CHANNEL")
        }
        return "$appChannel"
    }

}