package com.xiaosw.api.util;

import android.os.Build;

/**
 * @ClassName {@link OppoUtils}
 * @Description
 *
 * @Date 2020-12-30.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
public class OppoUtils {
    public static boolean isOppo5() {
        return RomUtils.isOppo() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isOppo10() {
        return RomUtils.isOppo() && Build.VERSION.SDK_INT >= 28;
    }

    public static boolean isOppo() {
        return RomUtils.isOppo();
    }
}
