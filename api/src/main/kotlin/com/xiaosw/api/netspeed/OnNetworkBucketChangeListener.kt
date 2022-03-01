package com.xiaosw.api.netspeed

/**
 * ClassName: [OnNetworkBucketChangeListener]
 * Description:
 *
 * Create by X at 2022/03/01 11:23.
 */
interface OnNetworkBucketChangeListener {
    fun onNetworkBucketChange(type: NetworkType, avg: Float, max: Long, min: Long)
}