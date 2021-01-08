package com.xiaosw.api.proxy

import android.app.Activity
import android.os.Bundle
import com.xiaosw.api.logger.Logger

/**
 * @ClassName: [ProxyActivitys]
 * @Description:
 *
 * Created by admin at 2021-01-08
 * @Email xiaosw0802@163.com
 */
internal class StandardActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.i("onCreate")
    }
}

internal class SingleTopActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.i("onCreate")
    }
}

internal class SingleTaskActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.i("onCreate")
    }
}

internal class SingleInstanceActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.i("onCreate")
    }
}