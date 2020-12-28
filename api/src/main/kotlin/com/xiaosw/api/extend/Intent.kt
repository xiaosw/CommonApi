package com.xiaosw.api.extend

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

/**
 * @ClassName: [Intent]
 * @Description:
 *
 * Created by admin at 2020-12-25
 * @Email xiaosw0802@163.com
 */
inline fun Intent.checkActivityValid(context: Context) = context
    .packageManager
    .queryIntentActivities(this, PackageManager.MATCH_DEFAULT_ONLY)
    .isNotEmpty()

inline fun Intent.checkServiceValid(context: Context) = context
    .packageManager
    .queryIntentServices(this, PackageManager.MATCH_DEFAULT_ONLY)
    .isNotEmpty()

inline fun Intent.checkReceiverValid(context: Context) = context
    .packageManager
    .queryBroadcastReceivers(this, PackageManager.MATCH_DEFAULT_ONLY)
    .isNotEmpty()