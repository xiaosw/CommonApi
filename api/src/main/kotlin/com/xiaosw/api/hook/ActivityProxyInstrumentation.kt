package com.xiaosw.api.hook

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent

/**
 * @ClassName: [ActivityProxyInstrumentation]
 * @Description:
 *
 * Created by admin at 2021-01-07
 * @Email xiaosw0802@163.com
 */
class ActivityProxyInstrumentation : Instrumentation() {

    override fun startActivitySync(intent: Intent?): Activity {
        return super.startActivitySync(intent)
    }

}