package com.doudou.component.open

import com.doudou.component.ComponentManager
import com.doudou.component.util.Dispatcher
import com.doudou.component.util.Standard

/**
 * ClassName: [Callback]
 * Description:
 *
 * Create by X at 2022/06/24 11:01.
 */
interface Callback<R> {

    fun providerResponseClass() : Class<R>? = null

    fun onSuccess(result: R)

    fun onFailure(code: Int? = CODE_ERROR_UNDEFINE, reason: String?)

    companion object {
        const val CODE_ERROR_UNDEFINE = -1
    }

}

inline fun <T> Callback<T>?.safeCallSuccess(t: T, mustInMain: Boolean = true) = this?.run {
    if (!mustInMain || (mustInMain && ComponentManager.use(Standard::class.java).isMainThread())) {
        onSuccess(t)
        return@run
    }
    ComponentManager.use(Dispatcher::class.java).postToMainThread {
        onSuccess(t)
    }
}

@JvmOverloads
inline fun <T> Callback<T>?.safeCallFail(
    code: Int? = Callback.CODE_ERROR_UNDEFINE,
    reason: String?,
    mustInMain: Boolean = true
) = this?.run {
    if (!mustInMain || (mustInMain && ComponentManager.use(Standard::class.java).isMainThread())) {
        onFailure(code, reason)
        return@run
    }
    ComponentManager.use(Dispatcher::class.java).postToMainThread {
        onFailure(code, reason)
    }
}