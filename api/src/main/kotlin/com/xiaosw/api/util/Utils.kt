package com.xiaosw.api.util

import com.xiaosw.api.extend.tryCatch
import java.io.Closeable

/**
 * @ClassName: [Utils]
 * @Description:
 *
 * Created by admin at 2020-09-11
 * @Email xiaosw0802@163.com
 */
object Utils {

    @JvmStatic
    fun safeClose(vararg closeables: Closeable?) {
        closeables?.forEach {
            it?.tryCatch { closable ->
                closable.close()
            }
        }
    }

}