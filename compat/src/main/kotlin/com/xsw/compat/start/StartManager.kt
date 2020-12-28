package com.xsw.compat.start

import android.content.Intent
import androidx.annotation.Keep
import com.xsw.compat.start.delegate.StartDelegate


/**
 * @ClassName: [StartManager]
 * @Description:
 *
 * Created by admin at 2020-12-25
 * @Email xiaosw0802@163.com
 */
@Keep
object StartManager {

    private val mDelegate by lazy {
        StartDelegate.create()
    }

    fun startActivity(intent: Intent?) = mDelegate.startActivity(intent)
}