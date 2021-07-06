package com.xiaosw.api.manager

import android.os.Looper
import com.xiaosw.api.extend.isMainThread
import com.xiaosw.api.util.WeakHandler
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

/**
 * ClassName: [DispatcherManager]
 * Description:
 *
 * Create by X at 2021/07/05 17:27.
 */
object DispatcherManager {

    val mainHandler by lazy {
        WeakHandler(Looper.getMainLooper())
    }

    // 此处不要开启太多的线程数量，如果存在cpu瓶颈的话，负担更重
    // 该方法依赖于当前设备的cpu核数，但是并不准确，返回的是操作系统设置的值
    // private int maxCount = Runtime.getRuntime().availableProcessors();
    val diskIOExecutor by lazy {
        Executors.newFixedThreadPool(2, object : ThreadFactory {
            var s = System.getSecurityManager()
            private val poolNumber = AtomicInteger(1)
            private val group = s?.threadGroup ?: Thread.currentThread().threadGroup
            private val threadNumber = AtomicInteger(1)
            private val namePrefix = "app-disk-pool-" + poolNumber.getAndIncrement() + "-thread-disk-fd-io"
            override fun newThread(r: Runnable): Thread {
                val t: Thread = object : Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0) {
                    override fun run() {
                        try {
                            super.run()
                        } catch (t: Throwable) {
                            //这里会被人用作线程嵌套 导致程序崩溃 暂时不做处理
                        }
                    }
                }
                if (t.isDaemon) {
                    t.isDaemon = false
                }
                if (t.priority != Thread.NORM_PRIORITY) {
                    t.priority = Thread.NORM_PRIORITY
                }
                return t
            }
        })
    }

    val netIOExecutor by lazy {
        Executors.newScheduledThreadPool(5, object : ThreadFactory {
            var s = System.getSecurityManager()
            private val poolNumber = AtomicInteger(1)
            private val group = s?.threadGroup ?: Thread.currentThread().threadGroup
            private val threadNumber = AtomicInteger(1)
            private val namePrefix = "app-net-pool-" + poolNumber.getAndIncrement() + "-thread-net-fd-io"
            override fun newThread(r: Runnable): Thread {
                val t: Thread = object : Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0) {
                    override fun run() {
                        try {
                            super.run()
                        } catch (t: Throwable) {
                            //这里会被人用作线程嵌套 导致程序崩溃 暂时不做处理
                        }
                    }
                }
                if (t.isDaemon) {
                    t.isDaemon = false
                }
                if (t.priority != Thread.NORM_PRIORITY) {
                    t.priority = Thread.NORM_PRIORITY
                }
                return t
            }
        })
    }

    // 定时任务
    private val mTimerExecutor = Executors.newFixedThreadPool(1)

    fun executeOnDiskIO(runnable: Runnable?) {
        diskIOExecutor.execute(runnable)
    }

    fun executeOnNetIO(runnable: Runnable?) {
        netIOExecutor.execute(runnable)
    }

    fun postToMainThread(run: () -> Unit) {
        if (isMainThread()) {
            run.invoke()
            return
        }
        mainHandler.post {
            run.invoke()
        }
    }

    fun postToMainThread(runnable: Runnable, delayMillis: Long)
    = mainHandler.postDelayed(runnable, delayMillis)

    fun removeCallbackFromMainThread(runnable: Runnable?) = mainHandler.removeCallbacks(runnable)

    fun removeAllCallbacksAndMessages() = mainHandler.removeCallbacksAndMessages(null)

    fun executeOnTimer(runnable: Runnable) = mTimerExecutor.execute(runnable)

}