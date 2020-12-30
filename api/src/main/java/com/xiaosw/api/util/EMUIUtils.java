package com.xiaosw.api.util;

import android.os.Build;

/**
 * @ClassName {@link EMUIUtils}
 * @Description
 *
 * @Date 2020-12-30.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
public class EMUIUtils {
    public static boolean isEMUI() {
        return RomUtils.isEmui();
    }

    public static boolean isEMUI9() {
//        return RomUtils.isEmui() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;
        return RomUtils.isEmui() && Build.VERSION.SDK_INT >= 28;
    }
}
