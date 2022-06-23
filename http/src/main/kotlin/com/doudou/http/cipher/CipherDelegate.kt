package com.doudou.http.cipher

import com.xiaosw.api.extend.trimEmptyOrNullChar

/**
 * ClassName: [CipherDelegate]
 * Description:
 *
 * Create by X at 2021/06/25 16:56.
 */
abstract class CipherDelegate {
    fun en(params: MutableMap<String?, Any?>?, type: CipherType): MutableMap<String?, Any?>? {
        if (type == CipherType.JAVA_BASE_64) {
            return javaBase64En(params)
        }
        return if (type == CipherType.JAVA_BP_SIGN) {
            javaBpSingEn(params)
        } else params
    }

    /**
     * java base64 加密
     * @param params
     * @return
     */
    abstract fun javaBase64En(params: MutableMap<String?, Any?>?): MutableMap<String?, Any?>?

    /**
     * java bp sign 加密
     * @param params
     * @return
     */
    abstract fun javaBpSingEn(params: MutableMap<String?, Any?>?): MutableMap<String?, Any?>?

    fun de(params: String, type: CipherType): String {
        if (params.trimEmptyOrNullChar() || type == CipherType.NONE) {
            return params
        }
        if (CipherType.JAVA_BASE_64 == type) {
            return javaBase64De(params)
        }
        return if (CipherType.JAVA_BP_SIGN == type) {
            javaBpSingDe(params)
        } else params
    }

    /**
     * java base64 解密
     * @param params
     * @return
     */
    abstract fun javaBase64De(params: String?): String

    /**
     * java bp sign 解密
     * @param params
     * @return
     */
    abstract fun javaBpSingDe(params: String?): String

    companion object {
        val DEF_CIPHER by lazy {
            DefCipherImpl()
        }
    }
}