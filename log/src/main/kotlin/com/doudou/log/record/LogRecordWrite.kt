package com.doudou.log.record

import android.util.Log
import com.doudou.log.Logger
import com.doudou.log.internal.LogThreadManager
import com.doudou.log.logi
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat

/**
 * ClassName: [LogRecordWrite]
 * Description:
 *
 * Create by X at 2022/01/21 18:12.
 */
internal class LogRecordWrite(private val logOutDir: String) : ILogRecordInternal {

    private val NEW_LINE_BYTES = "\n\n".toByteArray()
    private val mLevel by lazy {
        mutableMapOf<Int, String?>().also {
            it[Log.VERBOSE] = "V/"
            it[Log.DEBUG] = "D/"
            it[Log.INFO] = "I/"
            it[Log.WARN] = "W/"
            it[Log.ERROR] = "E/"
        }
    }

    private val YMDHMS by lazy {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
    }

    private val SDF by lazy {
        SimpleDateFormat("yyyy-MM-dd")
    }

    init {
        val startLog = "\n\n\n\n **************** launcher app at ${YMDHMS.format(System.currentTimeMillis())}  **************** \n"
        writeToDisk(logOutDir, logFileName(), startLog)
        logi {
            "log write to dir: $logOutDir"
        }
    }

    override fun onLogRecord(priority: Int, tag: String, msg: String): Boolean {
        writeToDisk(
            logOutDir,
            logFileName(),
            "${YMDHMS.format(System.currentTimeMillis())} ${mLevel[priority]}$tag\n$msg"
        )
        return true
    }

    override fun getLogFiles() = File(logOutDir).listFiles().filter {
        it.isFile && it.absolutePath.endsWith(LOG_SUFFIX)
    }

    override fun getLogOutDir() = logOutDir

    private fun logFileName() = "${SDF.format(System.currentTimeMillis())}$LOG_SUFFIX"

    private fun writeToDisk(outDir: String, fileName: String, content: String) {
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

    companion object {
        private const val LOG_SUFFIX = ".log"
    }
}