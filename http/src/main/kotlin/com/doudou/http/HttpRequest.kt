package com.doudou.http

import com.doudou.http.cipher.CipherType
import com.doudou.http.params.ClientParams
import com.xiaosw.api.extend.isNull
import com.xiaosw.api.extend.trimEmptyOrNullChar

/**
 * ClassName: [HttpRequest]
 * Description:
 *
 *
 * Create by X at 2021/06/25 16:12.
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
    params: MutableMap<String?, Any?>,
    header: MutableMap<String?, Any?>,
    retryCount: Int,
    addCommonParams: Boolean,
    addSecondCommonParams: Boolean
) {

    /**
     * 请求参数
     */
    private val mParams: MutableMap<String?, Any?>

    /**
     * 请求头参数
     */
    var headerMap: MutableMap<String?, Any?>

    /**
     * 重试次数
     */
    val retryCount: Int

    /**
     * 是否需要添加一级公参
     */
    val isAddCommonParams: Boolean

    /**
     * 是否需要添加二级公参
     */
    val isAddSecondCommonParams: Boolean
    val params: MutableMap<String?, Any?>
        get() = mParams
    val isValid: Boolean
        get() = !url.trimEmptyOrNullChar() && !method.isNull()

    class Builder {
        private var mUrl: String? = null
        private var mMethod: HttpMethod? = null
        private var mEncryptType: CipherType? = null
        private var mParams: MutableMap<String?, Any?>? = null
        private var mHeader: MutableMap<String?, Any?>? = null
        private var mRetryCount = 1
        private var addCommonParams = true
        private var addSecondCommonParams = true
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

        fun setAddCommonParams(addCommonParams: Boolean): Builder {
            this.addCommonParams = addCommonParams
            return this
        }

        fun setAddSecondCommonParams(addSecondCommonParams: Boolean): Builder {
            this.addSecondCommonParams = addSecondCommonParams
            return this
        }

        fun build(): HttpRequest {
            return HttpRequest(mUrl,
                    mMethod ?: HttpMethod.POST,
                    mEncryptType ?: CipherType.NONE,
                    mParams ?: ClientParams.EMPTY_COMMON_PARAMS,
                    mHeader ?: ClientParams.EMPTY_COMMON_PARAMS,
                    mRetryCount,
                    addCommonParams,
                    addSecondCommonParams)
        }
    }

    init {
        mParams = params
        headerMap = header
        this.retryCount = if (retryCount < 0) 0 else retryCount
        isAddCommonParams = addCommonParams
        isAddSecondCommonParams = addSecondCommonParams
        if (addCommonParams) {
            mParams.putAll(HttpManager.commonParams)
        }
        if (addSecondCommonParams) {
            mParams.putAll(HttpManager.secondCommonParams)
        }
    }
}