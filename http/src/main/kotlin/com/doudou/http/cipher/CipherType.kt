package com.doudou.http.cipher

/**
 * ClassName: [CipherType]
 * Description:
 *
 *
 * Create by X at 2021/06/25 15:33.
 */
enum class CipherType(private val encryptType: String) {
    NONE("none")
    , JAVA_BASE_64("java_base_64")
    , JAVA_BP_SIGN("java_bp_sign");
}