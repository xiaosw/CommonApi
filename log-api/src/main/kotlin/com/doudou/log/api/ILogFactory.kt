package com.doudou.log.api

import com.doudou.log.LogConfig

/**
 * ClassName: [ILogFactory]
 * Description:
 *
 * Create by X at 2022/06/21 10:04.
 */
interface ILogFactory {

    fun create(config: LogConfig) : ILog

}