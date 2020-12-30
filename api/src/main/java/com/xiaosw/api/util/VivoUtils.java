package com.xiaosw.api.util;

import android.os.Build;

/**
 * @ClassName {@link VivoUtils}
 * @Description
 *
 * @Date 2020-12-30.
 * @Author xiaosw<xiaosw0802@163.com>.
 */
public class VivoUtils {
    public static boolean isVivo8() {
        return RomUtils.isVivo() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    public static boolean isVivo() {
        return RomUtils.isVivo();
    }

    public static boolean isVivo10() {
        return RomUtils.isVivo() && Build.VERSION.SDK_INT >= 28;
    }
}
