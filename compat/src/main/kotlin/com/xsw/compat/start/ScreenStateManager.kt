package com.xsw.compat.start

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.annotation.Keep
import com.xiaosw.api.AndroidContext
import com.xiaosw.api.global.GlobalWeakHandler
import com.xiaosw.api.util.RomUtils
import com.xiaosw.api.util.WeakHandler
import java.util.concurrent.CopyOnWriteArraySet

/**
 * @ClassName: [ScreenStateManager]
 * @Description:
 *
 * Created by admin at 2020-12-30
 * @Email xiaosw0802@163.com
 */
@Keep
object ScreenStateManager {

    private val mListeners by lazy {
        CopyOnWriteArraySet<OnScreenStateChangeListener>()
    }

    private val mReceiver by lazy {
        ScreenStateChangeReceiver()
    }

    private val mCheckScreenStateTask by lazy {
        object : Runnable {
            override fun run() {
                GlobalWeakHandler.mainHandler.removeCallbacks(this)
            }
        }
    }

    init {
        registerReceiver()
    }

    private fun registerReceiver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            && (RomUtils.isOppo() || RomUtils.isVivo())) {
            GlobalWeakHandler.mainHandler.post(mCheckScreenStateTask)
            return
        }
        AndroidContext.get().registerReceiver(mReceiver, IntentFilter().also {
            with(it) {
                addAction(Intent.ACTION_SCREEN_OFF)
                addAction(Intent.ACTION_SCREEN_ON)
                addAction(Intent.ACTION_USER_PRESENT)
            }
        })
    }

    fun registerOnScreenStateChangeListener(listener: OnScreenStateChangeListener?) {
        listener?.run {
            if (mListeners.contains(this)) {
                return
            }
            mListeners.add(this)
        }
    }

    fun unregisterOnScreenStateChange(listener: OnScreenStateChangeListener?) {
        mListeners.remove(listener)
    }

    fun clear() {
        mListeners.clear()
    }

    interface OnScreenStateChangeListener {

        fun onScreenStateChange(action: String)

    }

    private class ScreenStateChangeReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.action?.run {
                mListeners.forEach {
                    it.onScreenStateChange(this)
                }
            }
        }

    }

}