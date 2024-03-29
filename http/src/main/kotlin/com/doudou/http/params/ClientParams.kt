package com.doudou.http.params

import androidx.annotation.Keep
import java.util.*

/**
 * ClassName: [ClientParams]
 * Description:
 *
 *
 * Create by X at 2021/06/25 17:16.
 */
@Keep
interface ClientParams {
    /**
     * 用户唯一标志
     * @return
     */
    val accid: String?

    /**
     * token
     * @return
     */
    val token: String?

    /**
     * 一级公参
     * @return
     */
    val commonParams: MutableMap<String?, Any?>?

    /**
     * 二级公参
     * @return
     */
    val secondCommonParams: MutableMap<String?, Any?>?

    companion object {
        val EMPTY_COMMON_PARAMS: MutableMap<String?, Any?> = mutableMapOf()
    }
}