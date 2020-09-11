package com.xiaosw.api.manager

import android.util.Log
import androidx.annotation.IntDef
import com.xiaosw.api.extend.isNull
import com.xiaosw.api.logger.Logger
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

/**
 * @ClassName: [ThreadManager]
 * @Description:
 *
 * Created by admin at 2020-09-11
 * @Email xiaosw0802@163.com
 */
object ThreadManager {

    private val WORK_CORE_SIZE = Runtime.getRuntime().availableProcessors() + 1
    private var mWorkExecutor: ExecutorService? = null
    private var mIoExecutor: ExecutorService? = null
    private val mLook by lazy {
        ReentrantLock()
    }

    private inline fun executor(@ThreadType type: Int) : ExecutorService {
        return try {
            mLook.lock()
            when(type) {
                THREAD_TYPE_IO -> {
                    if (mIoExecutor.isNull() || mIoExecutor?.isShutdown == true) {
                        mIoExecutor = Executors.newFixedThreadPool(WORK_CORE_SIZE * 2
                            , AppThreadFactory("app-io-thread"))
                    }
                    mIoExecutor!!
                }

                else -> {
                    if (mWorkExecutor.isNull() || mWorkExecutor?.isShutdown == true) {
                        mWorkExecutor = Executors.newFixedThreadPool(WORK_CORE_SIZE
                            , AppThreadFactory("app-work-thread"))
                    }
                    return mWorkExecutor!!
                }
            }
        } finally {
            mLook.unlock()
        }
    }

    @JvmStatic
    fun submit(@ThreadType type: Int = THREAD_TYPE_WORK, task: Runnable) = executor(type).submit(task)

    @JvmStatic
    fun <T> submit(@ThreadType type: Int = THREAD_TYPE_WORK, task: Callable<T>) = executor(type).submit<T>(task)

    @JvmStatic
    fun execute(@ThreadType type: Int = THREAD_TYPE_WORK, task: Runnable) = executor(type).execute(task)

    @JvmStatic
    fun isShutdown(@ThreadType type: Int) : Boolean {
        return when(type) {
            THREAD_TYPE_WORK -> {
                mWorkExecutor?.isShutdown ?: true
            }

            THREAD_TYPE_IO -> {
                mIoExecutor?.isShutdown ?: true
            }

            else -> true
        }
    }

    @JvmStatic
    fun shutdown(@ThreadType type: Int) {
        when(type) {
            THREAD_TYPE_WORK -> {
                internalShutdown(mWorkExecutor)
                mWorkExecutor = null
            }

            THREAD_TYPE_IO -> {
                internalShutdown(mIoExecutor, true)
                mIoExecutor = null
            }
        }
    }

    @JvmStatic
    fun shutdownNow(@ThreadType type: Int) {
        when(type) {
            THREAD_TYPE_WORK -> {
                internalShutdown(mWorkExecutor, true)
                mWorkExecutor = null
            }

            THREAD_TYPE_IO -> {
                internalShutdown(mIoExecutor, true)
                mIoExecutor = null
            }

            else -> null
        }
    }

    private inline fun internalShutdown(executor: ExecutorService?, now: Boolean = false) {
        executor?.let {
            if (it.isShutdown) {
                return
            }
            if (!now) {
                it.shutdown()
            }
            it.shutdownNow()
        }
    }

    @JvmStatic
    fun release() {
        try {
            mLook.lock()
            shutdownNow(THREAD_TYPE_WORK)
            mWorkExecutor = null
            shutdownNow(THREAD_TYPE_IO)
            mIoExecutor = null
        } finally {
            mLook.unlock()
        }
    }

    const val THREAD_TYPE_WORK = 1
    const val THREAD_TYPE_IO = 2

    @IntDef(THREAD_TYPE_WORK, THREAD_TYPE_IO)
    @Retention(RetentionPolicy.SOURCE)
    annotation class ThreadType

    class AppThreadFactory constructor(val name: String? = "app-thread") : ThreadFactory {

        private val count = AtomicInteger()

        override fun newThread(r: Runnable): Thread {
            val realName = "$name-${count.incrementAndGet()}"
            Logger.i("create thread >>> $realName")
            return Thread(r, realName)
        }

    }
}