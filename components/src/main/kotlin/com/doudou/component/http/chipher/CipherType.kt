package com.doudou.component.http.chipher

/**
 * ClassName: [CipherType]
 * Description:
 * MD5、SHA、HMAC这三种加密算法，可谓是非可逆加密，就是不可解密的加密方法。
 * 对称性加密算法有：AES、DES、3DES
 * 非对称性算法有：RSA、DSA、ECC
 * 其他常用算法：Base64
 * 定制算法：BP_SIGN、PHP
 *
 * Create by X at 2022/06/24 10:16.
 */
enum class CipherType(private val encryptType: String) {
    NONE("none")
    , AES("aes")
    , DES("des")
    , DES3("des3")
    , RSA("rsa")
    , DSA("dsa")
    , ECC("ecc")
    , BASE_64("base_64")
    , BP_SIGN("bp_sign")
    , PHP("php")
    , CUSTOM("custom");
}