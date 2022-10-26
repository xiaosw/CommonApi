package com.doudou.component.http

/**
 * ClassName: [HttpMethod]
 * Description:
 *
 * Create by X at 2022/06/24 10:11.
 */
enum class HttpMethod(val methodName: String) {

    GET("get"), POST("post");

    fun equals(text: String): Boolean {
        return methodName == text
    }

}