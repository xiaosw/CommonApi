package com.doudou.http.request

import com.doudou.http.HttpMethod
import com.doudou.http.HttpRequest
import com.doudou.http.cipher.CipherDelegate
import com.doudou.http.cipher.CipherType
import com.doudou.log.Logger
import com.xiaosw.api.delegate.CallbackDelegate
import com.xiaosw.api.delegate.CallbackDelegate2
import com.xiaosw.api.delegate.safeCallFail
import com.xiaosw.api.extend.isNull
import com.xiaosw.api.extend.tryCatch
import com.xiaosw.api.util.Utils.hasNull
import com.xiaosw.api.wrapper.GsonWrapper
import org.json.JSONObject
import java.lang.Exception

/**
 * ClassName: [HttpRequestDelegate]
 * Description:
 *
 * Create by X at 2021/07/05 14:41.
 */
abstract class HttpRequestDelegate {

    fun preCheckPostGzip(url: String?, content: String?, callback: CallbackDelegate2<*, MutableMap<String?, Any?>>? = null) : Boolean {
        if (hasNull(url)) {
            callback.safeCallFail(ERROR_URL_ILLEGAL,"url 异常")
            return false
        }
        if (hasNull(content)) {
            callback.safeCallFail(ERROR_URL_ILLEGAL,"content 异常")
            return false
        }
        return true
    }

    fun en(cipher: CipherDelegate, cipherType: CipherType?
           , content: MutableMap<String?, Any?>?) : MutableMap<String?, Any?> {
        if (content.isNullOrEmpty()) {
            return mutableMapOf()
        }
        return cipher?.tryCatch {
            cipherType?.let {
                cipher.en(content, cipherType)
            }
        } ?: content
    }

    fun de(cipher: CipherDelegate?, cipherType: CipherType?
           , content: String?) : String {
        if (content.isNullOrEmpty()) {
            return Global.EMPTY_STR
        }
        return cipher?.tryCatch { cipher ->
            if (content.contains("params")
                    && content.contains("ret")
                    && GsonWrapper.isJson(content)) { // 测试环境明文, 结果直接取 ret
                return JSONObject(content).optString("ret")
            }
            cipherType?.let {
                cipher.de(content, it)
            }
        } ?: content
    }

    fun request(httpRequest: HttpRequest,
                cipherDelegate: CipherDelegate,
                callback: CallbackDelegate2<String, MutableMap<String?, Any?>>? = null) {
        if (!httpRequest.isValid) {
            callback.safeCallFail(ERROR, "request params illegal!")
            return
        }
        if (httpRequest.encryptType.isNull()) {
            callback.safeCallFail(ERROR, "cipher is null!")
            return
        }
        try {
            val cipherType = httpRequest.encryptType
            request("${httpRequest.url}",
                    httpRequest.method,
                    httpRequest.retryCount,
                    en(cipherDelegate, cipherType, httpRequest.params),
                    httpRequest.headerMap,
                    cipherType,
                    cipherDelegate,
                    callback
            )
        } catch (e: Exception) {
            Logger.e(e)
            callback.safeCallFail(ERROR, e.message)
        }
    }

    protected abstract fun request(url: String,
                                   method: HttpMethod,
                                   retryCount: Int,
                                   ep: MutableMap<String?, Any?>,
                                   headerMap: MutableMap<String?, Any?>,
                                   cipherType: CipherType,
                                   cipherDelegate: CipherDelegate,
                                   callback: CallbackDelegate2<String, MutableMap<String?, Any?>>? = null)

    abstract fun postGzip(url: String?,
                          content: String?,
                          headerMap: MutableMap<String?, Any?>? = null,
                          callback: CallbackDelegate2<String, MutableMap<String?, Any?>>?)

    companion object {
        val DEF_HTTP_REQUEST by lazy {
            OkHttpRequestDelegateImpl()
        }

        const val ERROR = -8000
        const val ERROR_URL_ILLEGAL = -8001
    }

}