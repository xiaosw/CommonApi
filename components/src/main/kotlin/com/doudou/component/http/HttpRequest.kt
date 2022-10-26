package com.doudou.component.http

import com.doudou.component.http.chipher.CipherType

/**
 * ClassName: [HttpRequest]
 * Description:
 *
 * Create by X at 2022/06/24 10:13.
 */
class HttpRequest(
    /**
     * 请求地址
     */
    val url: String?,
    /**
     * 请求方法
     */
    val method: HttpMethod,
    /**
     * 加密方式
     */
    val encryptType: CipherType,
    /**
     * 请求参数
     */
    params: MutableMap<String?, Any?>,
    /**
     * 请求头
     */
    header: MutableMap<String?, Any?>,
    /**
     * 重试次数
     */
    retryCount: Int
) {

    val isValid: Boolean
        get() = null != url && null != method

    class Builder {
        private var mUrl: String? = null
        private var mMethod: HttpMethod? = null
        private var mEncryptType: CipherType? = null
        private var mParams: MutableMap<String?, Any?>? = null
        private var mHeader: MutableMap<String?, Any?>? = null
        private var mRetryCount = 1
        fun setUrl(url: String?): Builder {
            mUrl = url
            return this
        }

        fun setMethod(method: HttpMethod?): Builder {
            mMethod = method
            return this
        }

        fun setEncryptType(encryptType: CipherType?): Builder {
            mEncryptType = encryptType
            return this
        }

        fun setParams(params: MutableMap<String?, Any?>?): Builder {
            mParams = params
            return this
        }

        fun setHeader(header: MutableMap<String?, Any?>?): Builder {
            mHeader = header
            return this
        }

        fun setRetryCount(retryCount: Int): Builder {
            mRetryCount = retryCount
            return this
        }

        fun build(): HttpRequest {
            return HttpRequest(mUrl,
                mMethod ?: HttpMethod.POST,
                mEncryptType ?: CipherType.NONE,
                mParams ?: EMPTY_COMMON_PARAMS,
                mHeader ?: EMPTY_COMMON_PARAMS,
                if (mRetryCount >= 0) mRetryCount else 0)
        }
    }

    companion object {
        val EMPTY_COMMON_PARAMS = mutableMapOf<String?, Any?>()
    }
}