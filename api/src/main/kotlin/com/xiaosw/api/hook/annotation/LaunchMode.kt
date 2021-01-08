package com.xiaosw.api.hook.annotation

import androidx.annotation.IntDef
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * @ClassName: [LaunchMode]
 * @Description:
 *
 * Created by admin at 2021-01-08
 * @Email xiaosw0802@163.com
 */
@IntDef(
    LaunchMode.STANDARD,
    LaunchMode.SINGLE_TOP,
    LaunchMode.SINGLE_TASK,
    LaunchMode.SINGLE_INSTANCE
)
@Target(AnnotationTarget.FUNCTION)
@Retention(RetentionPolicy.SOURCE)
annotation class LaunchMode {

    companion object {
        const val STANDARD = 0
        const val SINGLE_TOP = 1
        const val SINGLE_TASK = 2
        const val SINGLE_INSTANCE = 3
    }

}