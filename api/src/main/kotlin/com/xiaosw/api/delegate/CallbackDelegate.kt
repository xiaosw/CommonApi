@file:JvmName("CallbackDelegateExt")
package com.xiaosw.api.delegate

import com.xiaosw.api.extend.isMainThread
import com.xiaosw.api.manager.DispatcherManager

/**
 * ClassName: [CallbackDelegate]
 * Description:
 *
 * Create by X at 2021/07/02 17:46.
 */
interface CallbackDelegate<R> {

    fun providerResponseClass() : Class<R>? = null

    fun onSuccess(result: R)

    fun onFailure(code: Int? = CODE_ERROR_UNDEFINE, reason: String?)

    companion object {
        const val CODE_ERROR_UNDEFINE = -1
    }
}

inline fun <T> CallbackDelegate<T>?.safeCallSuccess(t: T, mustInMain: Boolean = true) = this?.run {
    if (!mustInMain || (mustInMain && isMainThread())) {
        onSuccess(t)
        return@run
    }
    DispatcherManager.postToMainThread {
        onSuccess(t)
    }
}

@JvmOverloads
inline fun <T> CallbackDelegate<T>?.safeCallFail(
        code: Int? = CallbackDelegate.CODE_ERROR_UNDEFINE,
        reason: String?,
        mustInMain: Boolean = true
) = this?.run {
    if (!mustInMain || (mustInMain && isMainThread())) {
        onFailure(code, reason)
        return@run
    }
    DispatcherManager.postToMainThread {
        onFailure(code, reason)
    }
}