package com.xiaosw.api.config

import androidx.annotation.Keep
import com.doudou.log.LogConfig
import com.doudou.log.LogFormat
import com.doudou.log.Logger
import com.xiaosw.api.util.NetworkUtil

/**
 * @ClassName [AppConfig]
 * @Description
 *
 * @Date 2019-08-09.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
@Keep
object AppConfig {

    const val REQUEST_MIMETYPE_JSON = "application/json; charset=utf-8"

    /**
     * 单次点击间隔时间
     */
    const val SINGLE_CLICK_GAP_TIME = 300L
    /**
     * session 失效
     */
    const val SESSION_INVALID = 1000

    /**
     * 是否为 Debug 模式
     */
    var isDebug = false
        set(value) {
            field = value
            Logger.init(LogConfig(if (field) Logger.VERBOSE else Logger.NONE
                , "xsw---> "
                , LogConfig.MAX_LEN
                , LogFormat(enable = true, formatJson = true)))
            NetworkUtil.init()
        }

}