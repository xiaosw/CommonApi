package com.xiaosw.api.global

import android.os.Looper
import com.xiaosw.api.util.WeakHandler

/**
 * @ClassName: [GlobalWeakHandler]
 * @Description:
 *
 * Created by admin at 2020-12-30
 * @Email xiaosw0802@163.com
 */
object GlobalWeakHandler {

    val mainHandler by lazy {
        WeakHandler(Looper.getMainLooper())
    }

}