package com.xiaosw.api.hook

import android.content.Context
import com.xiaosw.api.AndroidContext
import com.xiaosw.api.extend.tryCatch
import com.xiaosw.api.hook.invocation.InvocationHandlerIntercept
import com.xiaosw.api.hook.invocation.ProxyInvocationHandler
import com.xiaosw.api.logger.Logger
import com.xiaosw.api.manager.WeakRegisterManager
import java.lang.reflect.Field
import java.lang.reflect.Proxy
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @ClassName: [BaseHook]
 * @Description:
 *
 * Created by admin at 2021-01-07
 * @Email xiaosw0802@163.com
 */
internal abstract class BaseHook :
    WeakRegisterManager.IRegisterManager<InvocationHandlerIntercept> {

    private val isHooked = AtomicBoolean(false)
    private val mHookResult = AtomicBoolean(false)
    private var mProxyInvocationHandler: ProxyInvocationHandler? = null

    internal fun hook(): Boolean {
        if (isHooked.get()) {
            return isHookSuccess()
        }
        val result = tryCatch {
            internalHook(AndroidContext.get())
        } ?: false
        isHooked.compareAndSet(false, true)
        mHookResult.set(result)
        return result
    }

    override fun register(intercept: InvocationHandlerIntercept) {
        hook()
        mProxyInvocationHandler?.register(intercept)
    }

    override fun unregister(intercept: InvocationHandlerIntercept) {
        mProxyInvocationHandler?.unregister(intercept)
    }

    override fun clear() {
        mProxyInvocationHandler?.clear()
    }

    fun isHookSuccess() = mHookResult.get()

    protected inline fun proxy(
        field: Field
        , obj: Any
        , proxyTarget: Any
    ) = tryCatch {
        mProxyInvocationHandler = ProxyInvocationHandler(proxyTarget, obj.toString())
        // Proxy
        val proxy = Proxy.newProxyInstance(
            AndroidContext.get().classLoader,
            proxyTarget::class.java.interfaces,
            mProxyInvocationHandler
        ) ?: return false

        // replace
        field.set(obj, proxy)
        return true
    } ?: false

    protected abstract fun internalHook(context: Context) : Boolean
}