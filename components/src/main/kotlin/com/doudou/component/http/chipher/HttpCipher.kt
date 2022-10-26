package com.doudou.component.http.chipher

/**
 * ClassName: [HttpCipher]
 * Description:
 *
 * Create by X at 2022/06/24 10:09.
 */
interface HttpCipher {

    fun ep(params: MutableMap<String?, Any?>?, type: CipherType): MutableMap<String?, Any?>? {
        return params?.let {
            when(type) {
                CipherType.AES -> aesEp(it)
                CipherType.DES -> desEp(it)
                CipherType.DES3 -> des3Ep(it)
                CipherType.RSA -> rsaEp(it)
                CipherType.DSA -> dsaEp(it)
                CipherType.ECC -> eccEp(it)
                CipherType.BASE_64 -> base64Ep(it)
                CipherType.BP_SIGN -> bpSignEp(it)
                CipherType.PHP -> phpEp(params)
                CipherType.CUSTOM -> customEp(params)
                else -> {
                    params
                }
            }
        } ?: params
    }

    fun dp(content: String, type: CipherType): String {
        return when(type) {
            CipherType.AES -> aesDp(content)
            CipherType.DES -> desDp(content)
            CipherType.DES3 -> des3Dp(content)
            CipherType.RSA -> rsaDp(content)
            CipherType.DSA -> dsaDp(content)
            CipherType.ECC -> eccDp(content)
            CipherType.BASE_64 -> base64Dp(content)
            CipherType.BP_SIGN -> bpSignDp(content)
            CipherType.PHP -> phpDp(content)
            CipherType.CUSTOM -> customDp(content)
            else -> {
                content
            }
        }
    }

    fun aesEp(params: MutableMap<String?, Any?>?): MutableMap<String?, Any?>?
    fun aesDp(content: String): String

    fun desEp(params: MutableMap<String?, Any?>?): MutableMap<String?, Any?>?
    fun desDp(content: String): String

    fun des3Ep(params: MutableMap<String?, Any?>?): MutableMap<String?, Any?>?
    fun des3Dp(content: String): String

    fun rsaEp(params: MutableMap<String?, Any?>?): MutableMap<String?, Any?>?
    fun rsaDp(content: String): String

    fun dsaEp(params: MutableMap<String?, Any?>?): MutableMap<String?, Any?>?
    fun dsaDp(content: String): String

    fun eccEp(params: MutableMap<String?, Any?>?): MutableMap<String?, Any?>?
    fun eccDp(content: String): String

    fun base64Ep(params: MutableMap<String?, Any?>?): MutableMap<String?, Any?>?
    fun base64Dp(content: String): String

    fun bpSignEp(params: MutableMap<String?, Any?>?): MutableMap<String?, Any?>?
    fun bpSignDp(content: String): String

    fun phpEp(params: MutableMap<String?, Any?>?): MutableMap<String?, Any?>?
    fun phpDp(content: String): String

    fun customEp(params: MutableMap<String?, Any?>?): MutableMap<String?, Any?>?
    fun customDp(content: String): String

}