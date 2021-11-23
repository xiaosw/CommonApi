package com.doudou.log.internal

import com.doudou.log.Logger
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * ClassName: [LogThreadManager]
 * Description:
 *
 * Create by X at 2021/11/23 10:37.
 */
internal object LogThreadManager {

    private val sLogThread by lazy {
        ThreadPoolExecutor(0,
            1,
            60L,
            TimeUnit.SECONDS,
            LinkedBlockingQueue(),
            AppThreadFactory()){ r, _ ->
            Logger.e("ignore: $r")
        }
    }

    fun execute(block: () -> Unit) = sLogThread.execute {
        block.invoke()
    }

    private class AppThreadFactory constructor(val name: String? = "App-logger") : ThreadFactory {

        private val mCount = AtomicInteger()

        override fun newThread(r: Runnable): Thread {
            val realName = "$name-${mCount.incrementAndGet()}"
            Logger.i("create thread >>> $realName")
            return Thread(r, realName)
        }

    }

}