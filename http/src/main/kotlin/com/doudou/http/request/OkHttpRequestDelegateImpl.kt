package com.doudou.http.request

import com.doudou.http.HttpMethod
import com.doudou.http.cipher.CipherDelegate
import com.doudou.http.cipher.CipherType
import com.doudou.http.interceptor.GzipInterceptor
import com.xiaosw.api.delegate.CallbackDelegate
import com.xiaosw.api.delegate.CallbackDelegate2
import com.xiaosw.api.delegate.safeCallFail
import com.xiaosw.api.delegate.safeCallSuccess
import okhttp3.*
import okio.BufferedSink
import java.io.DataOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.zip.GZIPOutputStream

/**
 * ClassName: [OkHttpRequestDelegateImpl]
 * Description:
 *
 * Create by X at 2021/07/05 14:43.
 */
open class OkHttpRequestDelegateImpl : HttpRequestDelegate() {

    private val mOkHttpClient by lazy {
        OkHttpClient.Builder()
                .connectTimeout(TIME_OUT_CONNECT, TimeUnit.MILLISECONDS)
                .readTimeout(TIME_OUT_READ, TimeUnit.MILLISECONDS)
//                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                .build()
    }

    private val mOkHttpClientWithGzip by lazy {
        OkHttpClient.Builder()
                .connectTimeout(TIME_OUT_CONNECT, TimeUnit.MILLISECONDS)
                .readTimeout(TIME_OUT_READ, TimeUnit.MILLISECONDS)
                .addInterceptor(GzipInterceptor())
//                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                .build()
    }

    override fun request(url: String, method: HttpMethod, retryCount: Int, ep: MutableMap<String?, Any?>,
                         headerMap: MutableMap<String?, Any?>, cipherType: CipherType,
                         cipherDelegate: CipherDelegate, callback: CallbackDelegate2<String, MutableMap<String?, Any?>>?) {
        if (method == HttpMethod.GET) {
            HttpUrl.parse(url)?.newBuilder()?.let { urlBuilder ->
                ep.keys?.forEach {
                    it?.let {
                        urlBuilder.addQueryParameter(it, ep[it]?.toString())
                    }
                }
                Request.Builder().url(urlBuilder.build()).get()
            } ?: null as? Request.Builder
        } else {
            Request.Builder().url(url).post(FormBody.Builder().also { formBuilder ->
                ep.keys?.forEach {
                    it?.apply {
                        formBuilder.add(it, ep[it]?.toString())
                    }
                }
            }.build())
        }?.also { builder ->
            headerMap.keys?.forEach {
                it?.let {
                    builder.addHeader(it, headerMap[it]?.toString())
                }
            }
        }?.build()?.run {
            mOkHttpClient.newCall(this)
                    .enqueue(buildOkCallback(mOkHttpClient, this, retryCount, cipherDelegate,
                            cipherType, callback))
        }
    }

    override fun postGzip(url: String?, content: String?,
                          headerMap: MutableMap<String?, Any?>?,
                          callback: CallbackDelegate2<String, MutableMap<String?, Any?>>?) {
        try {
            if (!preCheckPostGzip(url, content, callback)) {
                return
            }
            val request = Request.Builder().url(url).post(object : RequestBody() {
                override fun contentType(): MediaType? {
                    return MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8")
                }

                override fun writeTo(sink: BufferedSink) {
                    val out = DataOutputStream(sink.outputStream())
                    GZIPOutputStream(out).use {
                        it.write(content?.toByteArray())
                        it.flush()
                    }
                    out.use {
                        it.flush()
                    }
                }

            }).also { builder ->
                headerMap?.entries?.forEach {
                    it?.let {
                        builder.addHeader("${it.key}", it.value?.toString())
                    }
                }
            }.build()
            mOkHttpClientWithGzip.newCall(request)
                    .enqueue(buildOkCallback(mOkHttpClientWithGzip, request, 0,
                            CipherDelegate.DEF_CIPHER, null, callback))
        } catch (e: Exception) {
            callback.safeCallFail(ERROR, e.message)
        }
    }

    private inline fun buildOkCallback(
            client: OkHttpClient,
            request: Request,
            retryCount: Int = 0,
            cipher: CipherDelegate,
            cipherType: CipherType?,
            callback: CallbackDelegate2<String, MutableMap<String?, Any?>>?
    ) = object : Callback {

        private var requestCount = 0

        override fun onFailure(call: Call, e: IOException) {
            if (requestCount >= retryCount) {
                callback.safeCallFail(ERROR, e.message)
                return
            }
            requestCount++
            client.newCall(request).enqueue(this)
        }

        override fun onResponse(call: Call, response: Response?) {
            response?.body()?.string()?.let {
                val ext = mutableMapOf<String?, Any?>()
                response?.headers()?.let { headers ->
                    headers.names().forEach { headerKey ->
                        ext[headerKey] = headers.values(headerKey)
                    }
                }
                callback.safeCallSuccess(de(cipher, cipherType, it), ext)
            } ?: callback.safeCallFail(ERROR, "服务器异常")
        }
    }

    companion object {
        const val TIME_OUT_CONNECT = 15_000L
        const val TIME_OUT_READ = 15_000L
    }
}