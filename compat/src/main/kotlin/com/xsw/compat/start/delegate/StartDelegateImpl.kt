package com.xsw.compat.start.delegate

import android.content.Context
import android.content.Intent
import com.xiaosw.api.extend.checkActivityValid

/**
 * @ClassName: [StartDelegateImpl]
 * @Description:
 *
 * Created by admin at 2020-12-25
 * @Email xiaosw0802@163.com
 */
internal class StartDelegateImpl : StartDelegate() {

    override fun startActivity(context: Context, intent: Intent): Boolean {
        return intent.checkActivityValid(context)?.also {
            if (it) {
                context.startActivity(intent)
            }
        }
    }

}