package com.xiaosw.api.hook.intercept

import android.content.Intent
import com.xiaosw.api.AndroidContext
import com.xiaosw.api.hook.invocation.InvocationHandlerIntercept
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
            replaceIntent(args)
        }
    }

    companion object {
        private const val EXTRA_KEY_REPLACED_INTENT = "extra_key_replaced_intent"
        private const val EXTRA_KEY_ORIGINAL_INTENT = "extra_key_original_intent"

        internal fun replaceIntent(args: Array<Any?>) {
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
                args[originalIntentPosition] = Intent(AndroidContext.get(), StandardActivity::class.java)
                    .also { proxyIntent ->
                        proxyIntent.putExtra(EXTRA_KEY_REPLACED_INTENT, true)
                        proxyIntent.putExtra(EXTRA_KEY_ORIGINAL_INTENT, originalIntent)
                    }
            }
        }


    }
}