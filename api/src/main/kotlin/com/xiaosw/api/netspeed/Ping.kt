package com.xiaosw.api.netspeed

import com.doudou.log.loge
import com.doudou.log.logv
import com.xiaosw.api.extend.runOnUiThread
import com.xiaosw.api.manager.ThreadManager
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Serializable
import java.lang.Exception

/**
 * ClassName: [Ping]
 * Description:
 *
 * Create by X at 2021/08/24 18:52.
 */
class Ping(
        private val path: String,
        private val count: Int = 3,
        private val time: Float = 1F,
        private val callback: PingCallback? = null
) : Runnable {

    override fun run() {
        internalPing()
    }

    fun ping() = ThreadManager.executeNet(this)

    private fun internalPing() {
        var process: Process? = null
        try {
            val cmd = "ping -c $count -i $time $path"
            logv {
                "ping cmd = 【$cmd】"
            }
            process = Runtime.getRuntime()?.exec(cmd)?.also { process ->
                BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                    var temp: String? = null
                    val statistics = PingStatistics()
                    while (reader.readLine()?.also { temp = it } != null) {
                        val line = temp!!.toLowerCase()
                        logv {
                            "$line"
                        }
                        if (line.contains(STATISTICS_PACKETS_SUFFIX)
                                && line.contains(STATISTICS_RECEIVED_SUFFIX)
                                && line.contains(STATISTICS_LOSS_SUFFIX)
                                && line.contains(STATISTICS_TIME_PREFIX)) {
                            statistics.code = CODE_SUCCESS
                            parseStatisticsPackets(line, statistics)
                            continue
                        }
                        if (line.contains("min/avg/max/")) {
                            statistics.code = CODE_SUCCESS
                            parseStatisticsValue(line, statistics)
                            continue
                        }
                    }
                    callPingResult(statistics)
                    return
                }
            }
        } catch (e: Exception) {
            loge(e)
        } finally {
            process?.destroy()
        }
        callPingResult(PingStatistics(CODE_ERROR_UNKNOWN))
    }

    private fun callPingResult(
        result: PingStatistics
    ) = callback?.let {
        runOnUiThread {
            it.onPing(result)
        }
    }

    private fun parseStatisticsPackets(line: String, statistics: PingStatistics) {
        // 3 packets transmitted, 3 received, 0% packet loss, time 1002ms
        try {
            line.split(",").forEach {
                it?.trim()?.let { segment ->
                    when {
                        segment.endsWith(STATISTICS_PACKETS_SUFFIX) -> {
                            statistics.packets = safe2Int(segment.replace(STATISTICS_PACKETS_SUFFIX, ""))
                        }
                        segment.endsWith(STATISTICS_RECEIVED_SUFFIX) -> {
                            statistics.received = safe2Int(segment.replace(STATISTICS_RECEIVED_SUFFIX, ""))
                        }
                        segment.endsWith(STATISTICS_LOSS_SUFFIX) -> {
                            statistics.loss = safe2Float(segment.replace(STATISTICS_LOSS_SUFFIX, ""))
                        }
                        segment.startsWith(STATISTICS_TIME_PREFIX) -> {
                            statistics.time = safe2Long(segment.replace(STATISTICS_TIME_PREFIX, "").replace("ms", ""))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            loge(e)
        }
    }

    private fun parseStatisticsValue(line: String, statistics: PingStatistics) {
        // rtt min/avg/max/mdev = 30.842/66.188/119.332/38.255 ms
        try {
            var keys: List<String>? = null
            var values: List<String>? = null
            line.replace("rtt", "")
                    .replace("\\", "|")
                    .replace("/", "|")
                    .replace("ms", "")
                    .replace(" ", "")
                    .split("=").forEachIndexed { index, s ->
                        s?.let {
                            val list = it.split("|")
                            if (index === 0) {
                                keys = list
                            } else if (index === 1) {
                                values = list
                            }
                        }
                    }
            if (null == keys || values == null) {
                return
            }
            val k = keys!!
            val v = values!!
            if (k.size != v.size) {
                return
            }
            val map = mutableMapOf<String, String>()
            k.forEachIndexed { index, key ->
                map[key] = v[index]
            }
            with(statistics) {
                min = safe2Float(map["min"])
                avg = safe2Float(map["avg"])
                max = safe2Float(map["max"])
                mdev = safe2Float(map["mdev"])
            }
        } catch (e: Exception) {
            loge(e)
        }
    }

    private inline fun safe2Float(str: String?, def: Float = 0f) : Float {
        try {
            return str?.trim()?.toFloat() ?: def
        } catch (e: Exception) {}
        return def
    }

    private inline fun safe2Int(str: String?, def: Int = 0) : Int {
        try {
            return str?.trim()?.toInt() ?: def
        } catch (e: Exception) {}
        return def
    }

    private inline fun safe2Long(str: String?, def: Long = 0L) : Long {
        try {
            return str?.trim()?.toLong() ?: def
        } catch (e: Exception) {}
        return def
    }

    data class PingStatistics(
            var code: Int = CODE_ERROR_UNKNOWN,
            var packets: Int = 0,
            var received: Int = 0,
            var loss: Float = 0f,
            var time: Long = 0L,
            var min: Float = 0f,
            var avg: Float = 0f,
            var max: Float = 0f,
            var mdev: Float = 0f) : Serializable

    interface PingCallback {

        fun onPing(data: PingStatistics)

    }

    companion object {

        const val CODE_SUCCESS = 10000
        const val CODE_ERROR_UNKNOWN = 10001

        private const val STATISTICS_PACKETS_SUFFIX = "packets transmitted"
        private const val STATISTICS_RECEIVED_SUFFIX = "received"
        private const val STATISTICS_LOSS_SUFFIX = "% packet loss"
        private const val STATISTICS_TIME_PREFIX = "time"
        @JvmStatic
        @JvmOverloads
        fun ping(
                path: String,
                count: Int = 0,
                time: Float = 1F, // second
                callback: PingCallback? = null
        ) = try {
            Ping(path, count, time, callback).ping()
        } catch (e: Exception) {
            loge(e)
        }

    }

}