package com.xiaosw.api.hook.intercept

import android.app.Activity
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.os.Bundle
import com.xiaosw.api.AndroidContext
import com.xiaosw.api.extend.tryCatch
import com.xiaosw.api.hook.HookUtil
import com.xiaosw.api.hook.invocation.InvocationHandlerIntercept
import com.xiaosw.api.logger.Logger
import java.lang.ref.WeakReference
import java.util.*

/**
 * @ClassName: [ReceiverInvocationIntercept]
 * @Description:
 *
 * Created by admin at 2021-01-07
 * @Email xiaosw0802@163.com
 */
internal class ReceiverInvocationIntercept : InvocationHandlerIntercept
    , Application.ActivityLifecycleCallbacks {

    private val sReceiverRef by lazy {
        WeakHashMap<Context, WeakHashMap<BroadcastReceiver, Object>>()
    }

    init {
        AndroidContext.get().registerActivityLifecycleCallbacks(this)
    }

    override fun interceptInvoke(proxy: Any?, methodName: String, args: Array<out Any?>) {
        tryCatch(showException = false) {
            when(methodName) {
                "registerReceiver" -> {
                    handleRegisterReceiver(args)
                }

                "unregisterReceiver" -> {
                    handleUnregisterReceiver(args)
                }

                "startActivity" -> {
                    startActivity(args)
                }
            }
        }
    }

    private inline fun handleRegisterReceiver(args: Array<out Any?>) {
        if (null == args || args.size < 3) {
            return
        }
        // android.app.LoadedApk$ReceiverDispatcher$InnerReceiver@fcc76cc
        val innerReceiver = args[2] ?: return
        internalHandlerReceiver(innerReceiver) { success, context, receiver ->
            Logger.i("register: s = $success, c = $context, r = $receiver")
            if (!success) {
                return
            }
            var ref = sReceiverRef[context]
            if (null == ref) {
                ref = WeakHashMap()
                sReceiverRef[context] = ref
            }
            Logger.i("register: $receiver")
            ref[receiver] = null
        }
    }

    private inline fun handleUnregisterReceiver(args: Array<out Any?>) {
        if (null == args || args.isEmpty()) {
            return
        }
        // android.app.LoadedApk$ReceiverDispatcher$InnerReceiver@fcc76cc
        val innerReceiver = args[0] ?: return
        internalHandlerReceiver(innerReceiver) { success, context, receiver ->
            Logger.i("unregister: s = $success, c = $context, r = $receiver")
            if (!success) {
                return
            }
            val rec = receiver ?: return
            context?.tryCatch(showException = false) {
                Logger.i("unregister: $rec")
                sReceiverRef[context]?.remove(rec)
            }
        }
    }

    private inline fun internalHandlerReceiver(
        innerReceiver: Any
        , block : (success: Boolean, context: Context?, receiver: BroadcastReceiver?) -> Unit) {
        tryCatch {
            // android.app.LoadedApk$ReceiverDispatcher$InnerReceiver@fcc76cc
            val innerReceiverDispatcherRefField = HookUtil.getDeclaredField(innerReceiver::class.java
                , "mDispatcher")
            val innerReceiverDispatcherRef = HookUtil.get(innerReceiverDispatcherRefField, innerReceiver)
            val receiverDispatcher = (innerReceiverDispatcherRef as? WeakReference<Any?>)?.get()
            if (null == receiverDispatcher) {
                block.invoke( false, null, null)
                return
            }

            val mContextField = HookUtil.getDeclaredField(receiverDispatcher::class.java, "mContext")
            val mContext = HookUtil.get(mContextField, receiverDispatcher) as? Context
            if (null == mContext) {
                block.invoke( false, null, null)
                return
            }

            // android.app.LoadedApk$ReceiverDispatcher
            val mReceiverField = HookUtil.getDeclaredField(receiverDispatcher::class.java, "mReceiver")
            val mReceiver = (HookUtil.get(mReceiverField, receiverDispatcher) as? BroadcastReceiver)
            if (null == mReceiver) {
                block.invoke( false, null, null)
                return
            }
            block.invoke(true,  mContext, mReceiver)
        }
    }



    private inline fun startActivity(args: Array<out Any?>) {
        if (null == args) {
            return
        }

    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        sReceiverRef[activity]?.keys?.forEach { receiver ->
            receiver.tryCatch(showException = false) {
                activity.unregisterReceiver(it)
            }
        }
        sReceiverRef.remove(activity)
    }

}