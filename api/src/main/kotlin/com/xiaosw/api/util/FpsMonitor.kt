package com.xiaosw.api.util

import android.view.Choreographer
import androidx.annotation.Keep
import com.xiaosw.api.global.GlobalWeakHandler
import com.xiaosw.api.manager.WeakRegisterManager
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @ClassName: [FpsMonitor]
 * @Description:
 *
 * Created by admin at 2020-12-31
 * @Email xiaosw0802@163.com
 */
@Keep
object FpsMonitor {

    private const val FPS_INTERVAL_TIME = 1_000L

    private val isStarted = AtomicBoolean(false)

    private val mFpsTask by lazy {
        FpsTask()
    }

    private val mOnFpsMonitorListenerManager by lazy {
        WeakRegisterManager<OnFpsMonitorListener>()
    }

    @Volatile
    private var mFpsCount = 0

    fun start(listener: OnFpsMonitorListener? = null) {
        mOnFpsMonitorListenerManager.register(listener)
        if (isStarted.get()) {
            return
        }
        Choreographer.getInstance().postFrameCallback(mFpsTask)
        GlobalWeakHandler.mainHandler.postDelayed(mFpsTask, FPS_INTERVAL_TIME)
        isStarted.compareAndSet(false, true)
    }

    fun stop() {
        if (!isStarted.get()) {
            return
        }
        isStarted.compareAndSet(true, false)
        mFpsCount = 0
        Choreographer.getInstance().removeFrameCallback(mFpsTask)
        GlobalWeakHandler.mainHandler.removeCallbacks(mFpsTask)
    }

    fun registerOnFpsMonitorListener(listener: OnFpsMonitorListener?) {
        listener?.let {
            mOnFpsMonitorListenerManager.register(it)
        }
    }

    fun unregisterOnFpsMonitorListener(listener: OnFpsMonitorListener?) {
        listener?.let {
            mOnFpsMonitorListenerManager.unregister(it)
        }
    }

    fun clear() {
        mOnFpsMonitorListenerManager.clear()
    }

    private class FpsTask : Choreographer.FrameCallback, Runnable {
        override fun doFrame(frameTimeNanos: Long) {
            mFpsCount++
            Choreographer.getInstance().removeFrameCallback(this)
            Choreographer.getInstance().postFrameCallback(this)
        }

        override fun run() {
            mOnFpsMonitorListenerManager?.forEach {
                it.onFpsMonitor(mFpsCount)
            }
            mFpsCount = 0
            GlobalWeakHandler.mainHandler.postDelayed(this, FPS_INTERVAL_TIME)
        }
    }

    interface OnFpsMonitorListener {

        fun onFpsMonitor(fps: Int)

    }

}