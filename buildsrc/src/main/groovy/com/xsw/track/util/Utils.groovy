package com.xsw.track.util

class Utils {

    static boolean isNull(Object obj) {
        return obj == null
    }

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

    static void close(Closeable closeable) {
        if (isNull(closeable)) {
            return
        }
        try {
            closeable.close()
        } catch(Exception e) { }
    }

}