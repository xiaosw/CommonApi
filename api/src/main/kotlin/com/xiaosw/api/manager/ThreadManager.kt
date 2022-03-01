package com.xiaosw.api.manager

import com.doudou.log.Logger
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicLong
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
    private val mLook by lazy {
        ReentrantLock()
    }
    private val mExecutors by lazy {
        mutableMapOf<ThreadType, ExecutorService>()
    }

    private inline fun executor(type: ThreadType) : ExecutorService {
        return try {
            mLook.lock()
            return mExecutors[type]?.let {
                if (it.isShutdown) {
                    createExecutorIfNeeded(type)
                } else {
                    it
                }
            } ?: createExecutorIfNeeded(type)
        } finally {
            mLook.unlock()
        }
    }

    private fun createExecutorIfNeeded(type: ThreadType) : ExecutorService {
        val old = mExecutors[type]
        if (null != old && !old.isShutdown) {
            return old
        }
        return when(type) {
            ThreadType.THREAD_TYPE_IO -> {
                ThreadPoolExecutor(
                    0
                    , WORK_CORE_SIZE * 2
                    , 60
                    , TimeUnit.SECONDS
                    , LinkedBlockingQueue()
                    , AppThreadFactory("app-io-thread"))
            }

            ThreadType.THREAD_TYPE_NET -> {
                ThreadPoolExecutor(
                    0
                    , WORK_CORE_SIZE
                    , 60
                    , TimeUnit.SECONDS
                    , LinkedBlockingQueue()
                    , AppThreadFactory("app-net-thread"))
            }

            else -> {
                ThreadPoolExecutor(
                    WORK_CORE_SIZE
                    , WORK_CORE_SIZE
                    , 60
                    , TimeUnit.SECONDS
                    , LinkedBlockingQueue()
                    , AppThreadFactory("app-net-thread"))
            }
        }
    }

    @JvmStatic
    fun submit(type: ThreadType = ThreadType.THREAD_TYPE_WORK, task: Runnable) = executor(type).submit(
        task
    )

    @JvmStatic
    fun submitWork(task: Runnable) = executor(ThreadType.THREAD_TYPE_WORK).submit(
        task
    )

    @JvmStatic
    fun submitIo(task: Runnable) = executor(ThreadType.THREAD_TYPE_IO).submit(
        task
    )
    @JvmStatic
    fun submitNet(task: Runnable) = executor(ThreadType.THREAD_TYPE_NET).submit(
        task
    )


    @JvmStatic
    fun <T> submit(type: ThreadType = ThreadType.THREAD_TYPE_WORK, task: Callable<T>) = executor(type).submit(
        task
    )

    @JvmStatic
    fun <T> submitWork(task: Callable<T>) = executor(ThreadType.THREAD_TYPE_WORK).submit(
        task
    )

    @JvmStatic
    fun <T> submitIo(task: Callable<T>) = executor(ThreadType.THREAD_TYPE_IO).submit(
        task
    )

    @JvmStatic
    fun <T> submitNet(task: Callable<T>) = executor(ThreadType.THREAD_TYPE_NET).submit(
        task
    )

    @JvmStatic
    fun execute(type: ThreadType = ThreadType.THREAD_TYPE_WORK, task: Runnable) = executor(type).execute(
        task
    )

    @JvmStatic
    fun executeWork(task: Runnable) = executor(ThreadType.THREAD_TYPE_WORK).execute(
        task
    )

    @JvmStatic
    fun executeIo(task: Runnable) = executor(ThreadType.THREAD_TYPE_IO).execute(
        task
    )

    @JvmStatic
    fun executeNet(task: Runnable) = executor(ThreadType.THREAD_TYPE_NET).execute(
        task
    )

    @JvmStatic
    fun isShutdown(type: ThreadType) = mExecutors[type]?.isShutdown ?: true

    @JvmStatic
    fun shutdown(type: ThreadType) = internalShutdown(mExecutors[type]).also {
        mExecutors.remove(type)
    }

    @JvmStatic
    fun shutdownNow(type: ThreadType) = internalShutdown(mExecutors[type], true).also {
        mExecutors.remove(type)
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
            if (mExecutors.isEmpty()) {
                return
            }
            mExecutors.forEach {
                internalShutdown(it.value, false)
            }
            mExecutors.clear()
        } finally {
            mLook.unlock()
        }
    }

    enum class ThreadType {
        THREAD_TYPE_WORK,
        THREAD_TYPE_IO,
        THREAD_TYPE_NET
    }

    class AppThreadFactory constructor(val name: String? = "app-thread") : ThreadFactory {

        private val count = AtomicLong()

        override fun newThread(r: Runnable): Thread {
            val realName = "$name-${count.incrementAndGet()}"
            Logger.i("create thread >>> $realName")
            return Thread(r, realName)
        }

    }
}