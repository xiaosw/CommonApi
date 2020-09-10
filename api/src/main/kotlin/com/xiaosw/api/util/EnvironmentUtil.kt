package com.xiaosw.api.util

import android.os.Environment
import com.xiaosw.api.AndroidContext
import com.xiaosw.api.logger.Logger
import java.io.File

/**
 * @ClassName [EnvironmentUtil]
 * @Description
 *
 * @Date 2018-07-18.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
object EnvironmentUtil {

    private const val TAG = "EnvironmentUtil"
    private const val NO_MEDIA_FILE = ".nomedia"

    @JvmStatic
    fun hasSDCard() : Boolean = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState(), true)

    @JvmStatic
    @JvmOverloads
    fun appendSeparatorIfNeeded(path: String?) : String? {
        path?.let {
            return if (it.endsWith(File.separator)) it else "$it${File.separator}"
        }
        return path
    }

    /**
     * if exist, suffix is /, else return null
     */
    @JvmStatic
    @JvmOverloads
    fun getRootDir() : String? {
//        if (hasSDCard()) {
//            return appendSeparatorIfNeeded(Environment.getExternalStorageDirectory().absolutePath)
//        }
        AndroidContext.get().cacheDir?.run {
            return "${appendSeparatorIfNeeded(absolutePath)}xiaosw${File.separator}"
        }

        AndroidContext.get().filesDir?.run {
            return "${appendSeparatorIfNeeded(absolutePath)}xiaosw${File.separator}"
        }
        return null
    }

    @JvmStatic
    @JvmOverloads
    fun getPictureSaveDir(canUseRoot: Boolean = false) : String? {
        getRootDir()?.let {
            val dirFile = File("${it}Camera${File.separator}")
            if (createNewDirIfNotExists(dirFile)) {
                return appendSeparatorIfNeeded(dirFile.absolutePath)
            } else if (canUseRoot) {
                return it
            }
        }
        return null
    }

    @JvmStatic
    @JvmOverloads
    fun getPictureCompressCacheDir(canUseRoot: Boolean = true) : String? {
        getPictureSaveDir(false)?.let {
            val compressDirFile = File("${it}compress${File.separator}")
            if (createNewDirIfNotExists(compressDirFile)) {
                return appendSeparatorIfNeeded(compressDirFile.absolutePath)
            } else if (canUseRoot) {
                return it
            }
        }
        return null
    }

    @JvmStatic
    @JvmOverloads
    fun createNewDirIfNotExists(dir: File?) : Boolean {
        dir?.let {
            if (!it.exists()) {
                val mkdirs = it.mkdirs()
                Logger.e("createNewDirIfNotExists: create dir [ ${it.absolutePath} ] result = $mkdirs")
                return mkdirs
            }
            return true
        }
        return false
    }

    @JvmStatic
    @JvmOverloads
    fun createNewFile(file: File?, removeIfExists: Boolean = false) : Boolean {
        file?.let {
            if (it.exists()) {
                if (removeIfExists) {
                    it.delete()
                    val newFile = it.createNewFile()
                    Logger.e(TAG, "createNewFile: create file [ ${it.absolutePath} ] result = $newFile")
                    return newFile
                }
                return true
            } else {
                val newFile = it.createNewFile()
                Logger.e(TAG, "createNewFile: create file [ ${it.absolutePath} ] result = $newFile")
                return newFile
            }
        }
        return false
    }

    @JvmStatic
    @JvmOverloads
    fun createNoMediaFileIfNeeded(dir: String?) : Boolean {
        dir?.let {
            return createNoMediaFileIfNeeded(
                File(
                    "${appendSeparatorIfNeeded(
                        it
                    )}$NO_MEDIA_FILE"
                )
            )
        }
        return false
    }

    @JvmStatic
    @JvmOverloads
    fun createNoMediaFileIfNeeded(file: File?) : Boolean {
        return createNewFile(file)
    }

    @JvmStatic
    @JvmOverloads
    fun deleteFile(filePath: String) : Boolean = if (null == filePath) false else deleteFile(
        File(filePath)
    )

    @JvmStatic
    @JvmOverloads
    fun deleteFile(file: File?) : Boolean {
        file?.run {
            if (exists()) {
                if (isFile) {
                    delete()
                    Logger.i(TAG, "delete file [$absolutePath]")
                    return true
                } else {
                    Logger.w(TAG, "deleteFile: path [$absolutePath] is dir!")
                }
            } else {
                Logger.w(TAG, "deleteFile: path [$absolutePath] is not exists!")
                return true
            }
        }
        return false
    }

    @JvmStatic
    @JvmOverloads
    inline fun deleteDir(dirPath: String?) =  dirPath?.run {
        deleteDir(File(this))
    }

    @JvmStatic
    @JvmOverloads
    fun deleteDir(dir: File?) {
        dir?.run{
            if (!exists()) {
                return
            }
            if (isFile) {
                deleteFile(this)
                return
            }
            listFiles()?.forEach {
                if(it.isDirectory) {
                    deleteDir(it) // 递归删除目录
                } else {
                    deleteFile(it)
                }
            }
            delete() // 删除目录
            Logger.i(TAG, "delete dir [$absolutePath]")
        }
    }
}