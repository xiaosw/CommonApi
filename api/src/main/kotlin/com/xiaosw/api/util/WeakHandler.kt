package com.xiaosw.api.util

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import com.doudou.log.Logger
import com.xiaosw.api.extend.isNull
import com.xiaosw.api.extend.tryCatch
import java.lang.IllegalArgumentException
import java.lang.ref.WeakReference
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 * @ClassName: [WeakHandler]
 * @Description:
 *
 * Created by admin at 2020-12-30
 * @Email xiaosw0802@163.com
 */
class WeakHandler @JvmOverloads constructor (
    looper: Looper? = null
    , private val callback: Handler.Callback? = null
) {

    private val h = looper?.let {
        H(it, callback)
    } ?: H(callback)

    private val mLock by lazy {
        ReentrantLock()
    }

    private val mChainedRef by lazy {
        ChainedRef(mLock, null)
    }

    fun post(runnable: Runnable?) {
        runnable?.run() {
            wrapRunnable(runnable)?.let {
                h.post(it)
            }
        }
    }

    fun postAtTime(runnable: Runnable?,
                   uptimeMillis: Long = SystemClock.uptimeMillis()
    ) : Boolean {
        return wrapRunnable(runnable)?.let {
            h.postAtTime(it, uptimeMillis)
        } ?: false
    }

    fun postAtTime(
        runnable: Runnable?,
        token: Any?,
        uptimeMillis: Long = SystemClock.uptimeMillis()
    ) : Boolean {
        return wrapRunnable(runnable)?.let {
            h.postAtTime(it, token, uptimeMillis)
        } ?: false
    }

    fun postDelayed(runnable: Runnable?, delayMillis: Long = 0) : Boolean {
        return wrapRunnable(runnable)?.let {
            h.postDelayed(it, delayMillis)
        } ?: false
    }

    fun postAtFrontOfQueue(runnable: Runnable) : Boolean {
        return wrapRunnable(runnable)?.let {
            h.postAtFrontOfQueue(it)
        } ?: false
    }

    fun removeCallbacks(runnable: Runnable?, token: Any? = null) {
        mChainedRef.remove(runnable)?.also {
            h.removeCallbacks(it, token)
        }
    }

    fun sendMessage(msg: Message) = h.sendMessage(msg)

    fun sendEmptyMessage(what: Int) = h.sendEmptyMessage(what)

    fun sendEmptyMessageDelayed(what: Int, delayMillis: Long = 0) = h.sendEmptyMessageDelayed(
        what,
        delayMillis
    )

    fun sendEmptyMessageAtTime(what: Int, uptimeMillis: Long = SystemClock.uptimeMillis()) = h.sendEmptyMessageAtTime(
        what,
        uptimeMillis
    )

    fun sendMessageDelayed(msg: Message, delayMillis: Long = 0) = h.sendMessageDelayed(
        msg,
        delayMillis
    )

    fun sendMessageAtTime(msg: Message, uptimeMillis: Long = SystemClock.uptimeMillis()) = h.sendMessageAtTime(
        msg,
        uptimeMillis
    )

    fun sendMessageAtFrontOfQueue(msg: Message) = h.sendMessageAtFrontOfQueue(msg)

    fun removeMessages(what: Int, obj: Any? = null) = h.removeMessages(what, obj)

    fun removeCallbacksAndMessages(token: Any? = null) = h.removeCallbacksAndMessages(token)

    fun hasMessages(what: Int, obj: Any? = null) = h.hasMessages(what, obj)

    fun getLooper() : Looper = h.looper

    private inline fun wrapRunnable(runnable: Runnable?) : WeakRunnable? {
        return runnable?.let {
            val headRef = ChainedRef(mLock, it)
            mChainedRef.insert(headRef)
            headRef.mWeakRunnable
        } ?: null
    }

    private class H : Handler {

        private var mCallbackRef: WeakReference<Callback>? = null

        constructor() : super()

        constructor(callback: Callback?) : super() {
            mCallbackRef = WeakReference(callback)
        }

        constructor(looper: Looper, callback: Callback?) : super(looper) {
            mCallbackRef = WeakReference(callback)
        }

        override fun handleMessage(msg: Message) {
            mCallbackRef?.get()?.run {
                handleMessage(msg)
                return
            }
            super.handleMessage(msg)
        }
    }

    private class WeakRunnable(delegate: Runnable?, chainedRef: ChainedRef) : Runnable {

        val mDelegate: WeakReference<Runnable?> = WeakReference(delegate)
        private var mChainedRef: WeakReference<ChainedRef?> = WeakReference(chainedRef)

        override fun run() {
            mChainedRef?.get()?.remove()
            mDelegate?.get()?.run {
//                Logger.v("weak runnable run: $this")
                run()
            }
        }
    }

    private class ChainedRef(val lock: Lock, runnable: Runnable?) {
        private var mPrev: ChainedRef? = null
        private var mNext: ChainedRef? = null

        val mWeakRunnable = WeakRunnable(runnable, this)

        fun insert(candidate: ChainedRef?) {
            candidate?.let {
                lock.lock()
                tryCatch {
                    mNext?.let { next ->
                        next.mPrev = candidate
                    }
                    candidate.mNext = mNext
                    candidate.mPrev = this
                }
                lock.unlock()
            }
        }

        fun remove() : WeakRunnable {
            lock.lock()
            tryCatch {
                mPrev?.mNext = mNext
                mNext?.mPrev = mPrev
            }
            lock.unlock()
            return mWeakRunnable
        }

        fun remove(runnable: Runnable?): Runnable? {
            return runnable?.let {
                lock.lock()
                tryCatch {
                    var curr = mNext
                    while (!mNext.isNull()) {
                        if (curr?.mWeakRunnable?.mDelegate == runnable) {
                            return curr.remove()
                        }
                        curr = curr?.mNext
                    }
                }
                lock.unlock()
                null
            } ?: null
        }
    }
}