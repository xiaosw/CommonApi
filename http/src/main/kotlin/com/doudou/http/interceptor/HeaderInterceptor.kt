package com.doudou.http.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.*

/**
 * ClassName: [HeaderInterceptor]
 * Description:
 *
 *
 * Create by X at 2021/06/25 17:49.
 */
class HeaderInterceptor(private val headers: WeakHashMap<String?, String>?) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request()
                .newBuilder()
        headers?.keys?.forEach {
            it?.let {
                builder.addHeader(it, headers[it] ?: "")
            }
        }
        return chain.proceed(builder.build())
    }
}