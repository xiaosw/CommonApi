package com.xiaosw.api.manager

import android.annotation.TargetApi
import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.os.Build
import android.os.Bundle
import com.xiaosw.api.AndroidContext
import com.xiaosw.api.extend.tryCatch
import com.xiaosw.api.logger.Logger
import com.xiaosw.api.util.HookUtil
import com.xiaosw.api.util.Utils
import java.lang.ref.WeakReference
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.*
import kotlin.collections.HashSet

/**
 * @ClassName: [HookActivityManager]
 * @Description:
 * @Link https://blog.csdn.net/gdutxiaoxu/article/details/81459910
 *
 * Created by admin at 2021-01-06
 * @Email xiaosw0802@163.com
 */
internal object HookActivityManager : Application.ActivityLifecycleCallbacks {

    private val sReceiverRef by lazy {
        WeakHashMap<Context, WeakHashMap<BroadcastReceiver, Object>>()
    }


    fun hook() {
        AndroidContext.get().registerActivityLifecycleCallbacks(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            hookAMS26()
            return
        }
        hookAMS()
    }

    @TargetApi(Build.VERSION_CODES.O)
    private inline fun hookAMS26() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }
        with(ActivityManager::class.java) {
            // IActivityManagerSingleton
            val iActivityManagerSingleton =
                HookUtil.getDeclaredField(this, "IActivityManagerSingleton")?.get(null)
                    ?: return
            proxyAms(iActivityManagerSingleton)
        }
    }

    private inline fun hookAMS() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            hookAMS26()
            return
        }
        HookUtil.safe2Class("android.app.ActivityManagerNative")?.run {
            // ActivityManagerNative
            val gDefault =
                HookUtil.getDeclaredField(this, "gDefault")?.get(null)
                    ?: return
            proxyAms(gDefault)
        }
    }

    private inline fun proxyAms(target: Any) {
        // Singleton Field
        val mInstanceField = HookUtil.safe2Class("android.util.Singleton")
            ?.getDeclaredField("mInstance")?.also {
                it.isAccessible = true
            }
            ?: return

        // IActivityManager
        val iActivityManager = mInstanceField?.get(target) ?: return

        // handler
        val invocationHandler = ASMInvocationHandler(iActivityManager)

        // ActivityManager.class
        val iActivityManagerClazz = HookUtil.safe2Class("android.app.IActivityManager")
            ?: return

        // Proxy
        val proxy = Proxy.newProxyInstance(
            Thread.currentThread().contextClassLoader,
            arrayOf(iActivityManagerClazz),
            invocationHandler
        ) ?: return

        // replace
        mInstanceField.set(target, proxy)
    }

    private class ASMInvocationHandler(val proxyObj: Any) : InvocationHandler {

        override fun invoke(proxy: Any, method: Method, args: Array<Any?>): Any? {
            // printInvokeLog(method, args)
            tryCatch(showException = false) {
                when(method.name) {
                    "registerReceiver" -> handleRegisterReceiver(args)
                    "unregisterReceiver" -> handleUnregisterReceiver(args)
                }
            }
            return method.invoke(proxyObj, *args)
        }

        private inline fun handleRegisterReceiver(args: Array<Any?>) {
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

        private inline fun handleUnregisterReceiver(args: Array<Any?>) {
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

        private inline fun printInvokeLog(method: Method, args: Array<Any?>) {
            if (!Logger.isEnable()) {
                return
            }
            tryCatch(showException = false) {
                val sb = StringBuilder()
                args.forEachIndexed { index, any ->
                    sb.append("p$index：$any")
                    if (index < args.size - 1) {
                        sb.append(", ")
                    }
                }
                Logger.i("invoke: ${method.name}, $sb")
            }
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