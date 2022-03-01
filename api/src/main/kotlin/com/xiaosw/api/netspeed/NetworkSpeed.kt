package com.xiaosw.api.netspeed

import com.xiaosw.api.manager.ActivityLifeManager

/**
 * ClassName: [NetworkSpeed]
 * Description:
 *
 * Create by X at 2022/03/01 11:13.
 */
interface NetworkSpeed : ActivityLifeManager.AppLifecycleListener {

    fun startTrack()

    fun stopTrack()

    fun query(type: NetworkType, startTime: Long, endTime: Long) : NetworkBucket

    fun isActivity() : Boolean
}