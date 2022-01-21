package com.doudou.log.record

import android.content.Context
import com.doudou.log.LogConfig
import com.doudou.log.Logger
import com.doudou.log.internal.LogThreadManager
import com.doudou.log.logw
import com.doudou.log.logi
import java.io.*
import java.text.SimpleDateFormat


/**
 * ClassName: [LogRecordManager]
 * Description:
 *
 * Create by X at 2022/01/20 09:43.
 */
object LogRecordManager {

    private val NEW_LINE_BYTES = "\n\n".toByteArray()
    private const val LOG_SUFFIX = ".log"

    private val mLevel by lazy {
        mutableMapOf<Int, String?>().also {
            it[Logger.VERBOSE] = "V/"
            it[Logger.DEBUG] = "D/"
            it[Logger.INFO] = "I/"
            it[Logger.WARN] = "W/"
            it[Logger.ERROR] = "E/"
        }
    }

    private val YMDHMS by lazy {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
    }

    private val SDF by lazy {
        SimpleDateFormat("yyyy-MM-dd")
    }

    var logOutDir: String = ""
        private set

    fun init(context: Context, config: LogConfig) {
        if (!config.saveToDisk) {
            logw("app disable save log to disk.")
            return
        }
        context?.let {
            (it.externalCacheDir ?: it.cacheDir)?.absolutePath?.let { cacheDir ->
                if (cacheDir.isNotEmpty()) {
                    logOutDir = "$cacheDir/logger/record"
                    logi("app logger record dir: $logOutDir")
                }
            }
        }
        if (logOutDir.isNotEmpty()) {
            val startLog = "\n\n\n\n **************** launcher app at ${YMDHMS.format(System.currentTimeMillis())}  **************** \n"
            writeToDisk(logOutDir, logFileName(), startLog)
        }
    }

    fun getLogFiles() = File(logOutDir).listFiles().filter {
        it.absolutePath.endsWith(LOG_SUFFIX)
    }

    internal fun onLogRecord(priority: Int, tag: String, msg: String) : Boolean {
        if (logOutDir.isEmpty()) {
            return false
        }
        val currentTimeInMillis = System.currentTimeMillis()
        writeToDisk(
            logOutDir,
            logFileName(),
            "${YMDHMS.format(currentTimeInMillis)} ${mLevel[priority]}$tag\n$msg"
        )
        return true
    }

    private fun logFileName() = "${SDF.format(System.currentTimeMillis())}$LOG_SUFFIX"

    private fun writeToDisk(outDir: String, fileName: String, content: String) {
        if (content.isEmpty() || outDir.isEmpty() || fileName.isEmpty()) {
            return
        }
        LogThreadManager.startRecord {
            try {
                with(File(outDir)) {
                    if (!exists()) {
                        mkdirs()
                    }
                }
                val logFile = File("${outDir}${File.separator}${fileName}").also {
                    if (!it.exists()) {
                        it.createNewFile()
                    }
                }
                FileOutputStream(logFile, true).use {
                    it.write(content.toByteArray())
                    it.write(NEW_LINE_BYTES)
                    it.flush()
                }
            } catch (e: Exception) {
                Logger.e(e)
            }
        }
    }

}