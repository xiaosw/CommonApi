package com.xiaosw.api.hook.intercept

import android.content.Intent
import com.xiaosw.api.AndroidContext
import com.xiaosw.api.extend.tryCatch
import com.xiaosw.api.hook.HookUtil
import com.xiaosw.api.hook.annotation.LaunchMode
import com.xiaosw.api.hook.annotation.NotRegister
import com.xiaosw.api.hook.invocation.InvocationHandlerIntercept
import com.xiaosw.api.logger.Logger
import com.xiaosw.api.proxy.SingleInstanceActivity
import com.xiaosw.api.proxy.SingleTaskActivity
import com.xiaosw.api.proxy.SingleTopActivity
import com.xiaosw.api.proxy.StandardActivity

/**
 * @ClassName: [ActivityNotRegisterInvocationIntercept]
 * @Description:
 *
 * Created by admin at 2021-01-07
 * @Email xiaosw0802@163.com
 */
internal class ActivityNotRegisterInvocationIntercept : InvocationHandlerIntercept {

    override fun interceptInvoke(proxy: Any?, methodName: String, args: Array<Any?>) {
        if ("startActivity" == methodName) {
            replace2ProxyIntent(args)
        }
    }

    companion object {
        private const val EXTRA_KEY_REPLACED_INTENT = "extra_key_replaced_intent"
        private const val EXTRA_KEY_ORIGINAL_INTENT = "extra_key_original_intent"

        private const val INTENT_CMP = "Intent{cmp="

        internal fun replace2ProxyIntent(args: Array<Any?>) {
            Logger.e("replace2ProxyIntent: $args")
            var originalIntent: Intent? = null
            var originalIntentPosition = 0
            for (arg in args) {
                if (arg is Intent) {
                    originalIntent = arg
                    break
                }
                originalIntentPosition++
            }
            originalIntent?.let {
                val replaced = it.getBooleanExtra(
                    EXTRA_KEY_REPLACED_INTENT,
                    false
                )
                if (replaced) {
                    return
                }

                // Intent{cmp=}
                var intentStr = it.toString().replace(" ", "")
                var className: String? = null
                if (intentStr.startsWith(INTENT_CMP)) {
                    className = intentStr.substring(INTENT_CMP.length, intentStr.length - 1).let { cmp ->
                        if (cmp.contains("/.")) {
                            cmp.replace("/.", ".")
                        } else {
                            cmp.substring(cmp.indexOf("/"))
                        }
                    }
                }

                val clazz = HookUtil.safe2Class(className) ?: return
//                val launchMode = clazz.getAnnotation(NotRegister::class.java)?.launchMode ?: return
                val launchMode = LaunchMode.STANDARD
                val proxyActivityClazz = when(launchMode) {
                    LaunchMode.SINGLE_TOP -> SingleTopActivity::class.java
                    LaunchMode.SINGLE_TASK -> SingleTaskActivity::class.java
                    LaunchMode.SINGLE_INSTANCE -> SingleInstanceActivity::class.java
                    else -> StandardActivity::class.java
                }

                Logger.e("launchMode = $launchMode" +
                        ", className : $className" +
                        ", intentStr = $intentStr"
                    , ActivityNotRegisterInvocationIntercept::class.java.simpleName)

                args[originalIntentPosition] = Intent(AndroidContext.get(), proxyActivityClazz)
                    .also { proxyIntent ->
                        proxyIntent.putExtra(EXTRA_KEY_REPLACED_INTENT, true)
                        proxyIntent.putExtra(EXTRA_KEY_ORIGINAL_INTENT, originalIntent)
                    }
            }
        }

        internal fun replace2OriginalIntent(obj: Any?) {
            Logger.e("replace2OriginalIntent: $obj")
            obj?.tryCatch(showException = false) {
                val proxyIntent =  HookUtil.getDeclaredField(it::class.java, "intent")
                    ?.get(it) as? Intent
                    ?: return@tryCatch

                if (!proxyIntent.getBooleanExtra(EXTRA_KEY_REPLACED_INTENT, false)) {
                    return@tryCatch
                }

                val originalIntent = proxyIntent.getParcelableExtra<Intent>(EXTRA_KEY_ORIGINAL_INTENT)
                    ?: return@tryCatch
                proxyIntent.component = originalIntent.component
                proxyIntent.removeExtra(EXTRA_KEY_REPLACED_INTENT)
            }
        }

    }
}