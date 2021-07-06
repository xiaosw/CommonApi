package com.doudou.http

import com.doudou.http.cipher.CipherDelegate
import com.doudou.http.location.ServerLocationManager
import com.doudou.http.params.ClientParams
import com.doudou.http.params.ClientParams.Companion.EMPTY_COMMON_PARAMS
import com.doudou.http.request.HttpRequestDelegate
import com.xiaosw.api.delegate.CallbackDelegate
import com.xiaosw.api.delegate.JsonDelegate
import com.xiaosw.api.delegate.safeCallFail
import com.xiaosw.api.delegate.safeCallSuccess
import com.xiaosw.api.extend.isNull
import com.xiaosw.api.extend.tryCatch
import com.xiaosw.api.manager.DispatcherManager

/**
 * ClassName: [HttpManager]
 * Description: 网络请求管理
 *
 *
 * Create by X at 2021/06/25 15:20.
 */
object HttpManager : ClientParams {
    private var mClientParams: ClientParams? = null
    private lateinit var mCipherDelegate: CipherDelegate
    private lateinit var mHttpRequestDelegate: HttpRequestDelegate

    var jsonParse: JsonDelegate = JsonDelegate.DEF

    ///////////////////////////////////////////////////////////////////////////
    // Single model end.
    ///////////////////////////////////////////////////////////////////////////
    @JvmOverloads
    @JvmStatic
    fun init(params: ClientParams?, cipher: CipherDelegate = CipherDelegate.DEF_CIPHER,
             httpRequest: HttpRequestDelegate = HttpRequestDelegate.DEF_HTTP_REQUEST) {
        mClientParams = params
        mCipherDelegate = cipher
        mHttpRequestDelegate = httpRequest
    }

    ///////////////////////////////////////////////////////////////////////////
    // params proxy api
    ///////////////////////////////////////////////////////////////////////////
    override val accid: String
        get() = mClientParams?.accid ?: ""

    override val token: String
        get() = mClientParams?.token ?: ""

    override val commonParams: Map<String?, String?>
        get() = mClientParams?.commonParams ?: EMPTY_COMMON_PARAMS

    override val secondCommonParams: Map<String?, String?>
        get() = mClientParams?.secondCommonParams ?: EMPTY_COMMON_PARAMS

    ///////////////////////////////////////////////////////////////////////////
    // Http api
    ///////////////////////////////////////////////////////////////////////////
    fun <T> request(request: HttpRequest, callback: CallbackDelegate<T>) {
        if (request.isNull() || !request.isValid) {
            safeCallFail(callback, "request params illegal!")
            return
        }
        val url = request.url
        val params = request.params
        if (url?.endsWith("external-adv-cloud-api/config/adv.config") == true
                || url?.endsWith("apppubliclogs/exterlog") == true) {
            with(params) {
                ServerLocationManager.getServerLocation()?.apply {
                    put("province", province)
                    put("city", city)
                    put("country", country)
                } ?: {
                    put("province", "null")
                    put("city", "null")
                    put("country", "null")
                }
            }
        }
        mHttpRequestDelegate.request(request, mCipherDelegate, object : CallbackDelegate<String> {

            override fun onSuccess(s: String) {
                tryCatch {
                    convertResponse(s, callback)?.let {
                        safeCallSuccess(callback, it)
                    } ?: safeCallFail(callback, "convert response error")
                    return
                }
            }

            override fun onFailure(code: Int?, reason: String?) {
                safeCallFail(callback, reason)
            }
        })
    }

    /**
     * Gzip请求 只支持post请求
     *
     * @param url
     * @param content
     * @param headerMap
     * @param callback
     */
    fun postGzip(url: String?, content: String?, headerMap: Map<String?, String?>? = null,
                 callback: CallbackDelegate<String>?) {
        mHttpRequestDelegate.postGzip(url, content, headerMap, object : CallbackDelegate<String>{
            override fun onSuccess(s: String) {
                callback.safeCallSuccess(s)
            }

            override fun onFailure(code: Int?, reason: String?) {
                callback.safeCallFail(code, reason)
            }
        })
    }

    private fun <T> safeCallSuccess(callback: CallbackDelegate<T>, response: T) {
        DispatcherManager.postToMainThread {
            callback.safeCallSuccess(response)
        }
    }

    private fun safeCallFail(callback: CallbackDelegate<*>, message: String?) {
        DispatcherManager.postToMainThread {
            callback.safeCallFail(reason = message)
        }
    }

    private fun <T> convertResponse(result: String?, callback: CallbackDelegate<T>): T? {
        if (result.isNullOrEmpty() || callback.isNull()) {
            return null
        }
        return tryCatch {
            callback.providerResponseClass()?.let {
                return jsonParse.fromJson(result, it)
            }
            return jsonParse.parseType(callback.javaClass)?.let {
                jsonParse.fromJson(result, it)
            } ?: null
        } ?: null
    }
}