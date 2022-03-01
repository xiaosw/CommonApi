package com.xiaosw.api.netspeed

/**
 * ClassName: [NetworkBucket]
 * Description:
 *
 * Create by X at 2022/03/01 11:18.
 */
data class NetworkBucket(
    val type: Int,
    var tx: Long = 0L,
    var rx: Long = 0L
)