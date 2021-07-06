package com.doudou.http

/**
 * ClassName: [HttpMethod]
 * Description: 请求方法
 *
 *
 * Create by X at 2021/06/25 15:20.
 */
enum class HttpMethod(private val mMethodName: String) {
    GET("get"), POST("post");

    fun methodName(): String {
        return mMethodName
    }

    fun equals(text: String): Boolean {
        return mMethodName == text
    }
}