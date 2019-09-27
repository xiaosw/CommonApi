package com.xiaosw.api.extend

import android.os.Build

/**
 * @ClassName [Device]
 * @Description
 *
 * @Date 2019-04-26.
 * @Author xiaosw<xiaosw0802@163.com>.
 */

/** >= Gingerbread 9 2.3 */
fun hasGingerbread(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD
}

/**
 * >= Honeycomb 11 3.0
 */
fun hasHoneycomb() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB

/** >= 4.0 14  */
fun hasICS(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
}

/**
 * >= JellyBean 16 4.1
 */
fun  hasJellyBean() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN

/**
 * >= KITKAT 19 4.4
 */
fun  hasKitkat() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

/**
 * >= LOLLIPOP 21 5.1
 */
fun  hasLollipop() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

/**
 * >= 23 6.0
 */
fun hasMarshmallow() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M