package com.xiaosw.api.delegate

import com.xiaosw.api.extend.isMainThread
import com.xiaosw.api.manager.DispatcherManager

/**
 * ClassName: [CallbackDelegate2]
 * Description:
 *
 * Create by X at 2022/06/23 19:36.
 */
interface CallbackDelegate2<R, EXT> {

    fun onSuccess(result: R, ext: EXT? = null)

    fun onFailure(code: Int? = CODE_ERROR_UNDEFINE, reason: String?)

    companion object {
        const val CODE_ERROR_UNDEFINE = -1
    }
}

inline fun <R, EXT> CallbackDelegate2<R, EXT>?.safeCallSuccess(
    result: R, ext: EXT? = null, mustInMain: Boolean = true
) = this?.run {
    if (!mustInMain || (mustInMain && isMainThread())) {
        onSuccess(result, ext)
        return@run
    }
    DispatcherManager.postToMainThread {
        onSuccess(result, ext)
    }
}

@JvmOverloads
inline fun <T, EXT> CallbackDelegate2<T, EXT>?.safeCallFail(
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