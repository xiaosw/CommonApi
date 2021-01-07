package com.xiaosw.api.hook.invocation

import com.xiaosw.api.extend.tryCatch
import com.xiaosw.api.logger.Logger
import com.xiaosw.api.manager.WeakRegisterManager
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
/**
 * @ClassName: [ProxyInvocationHandler]
 * @Description:
 *
 * Created by admin at 2021-01-07
 * @Email xiaosw0802@163.com
 */
internal class ProxyInvocationHandler(
    private val target: Any? = null
) : InvocationHandler, WeakRegisterManager.IRegisterManager<InvocationHandlerIntercept> {

    private val mIntercepts = WeakRegisterManager<InvocationHandlerIntercept>()

    override fun invoke(proxy: Any?, method: Method, args: Array<Any?>?) : Any? {
        val arguments = args ?: emptyArray()
        printInvokeLog(method, arguments)
        val realProxy = target ?: proxy
        mIntercepts?.forEach { intercept ->
            tryCatch(showException = false) {
                intercept?.interceptInvoke(realProxy, method.name, arguments)
            }
        }
        return method.invoke(realProxy, *arguments)
    }

    override fun register(intercept: InvocationHandlerIntercept)
            = mIntercepts.register(intercept)

    override fun unregister(intercept: InvocationHandlerIntercept)
            = mIntercepts.unregister(intercept)

    override fun clear()= mIntercepts.clear()

    private inline fun printInvokeLog(method: Method, args: Array<out Any?>) {
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
            Logger.e("invoke: ${method.name}, $sb", "ProxyInvocationHandler")
        }
    }

}

