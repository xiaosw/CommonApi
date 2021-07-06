package com.doudou.http.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * ClassName: [GzipInterceptor]
 * Description:
 *
 *
 * Create by X at 2021/06/25 17:49.
 */
class GzipInterceptor : Interceptor {
    
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
                chain.request()
                        .newBuilder()
                        .header("Content-Encoding", "gzip")
                        .build()
        )
    }
}