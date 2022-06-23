package com.doudou.http.cipher

/**
 * ClassName: [DefCipherImpl]
 * Description:
 *
 *
 * Create by X at 2021/06/25 17:16.
 */
class DefCipherImpl : CipherDelegate() {
    override fun javaBase64En(params: MutableMap<String?, Any?>?): MutableMap<String?, Any?>? {
        return params
    }

    override fun javaBpSingEn(params: MutableMap<String?, Any?>?): MutableMap<String?, Any?>?? {
        return params
    }

    override fun javaBase64De(params: String?): String {
        return params ?: Global.EMPTY_STR
    }

    override fun javaBpSingDe(params: String?): String {
        return params ?: Global.EMPTY_STR
    }
}