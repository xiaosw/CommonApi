package com.xiaosw.api.extend

import android.app.Activity
import com.xiaosw.api.util.ScreenUtil

/**
 * @ClassName [Activity]
 * @Description
 *
 * @Date 2019-04-26.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
inline fun Activity.statusBarHeight() = ScreenUtil.getStatusHeight(this)