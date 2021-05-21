package com.xsw.track.util

class Utils {

    static boolean hasNull(Object... args) {
        if (null == args) {
            return true
        }
        for (Object obj: args) {
            if (null == obj) {
                return true
            }
        }
        return false
    }

}