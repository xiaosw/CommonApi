package com.xiaosw.api.util;

import android.os.Build;

/**
 * @ClassName {@link MIUIUtils}
 * @Description
 *
 * @Date 2020-12-30.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
public class MIUIUtils {

    public static boolean isMIUI9() {
        return RomUtils.isMiui() && Build.VERSION.SDK_INT >= 28;
    }

    public static boolean isMIUI() {
        return RomUtils.isMiui();
    }
}
