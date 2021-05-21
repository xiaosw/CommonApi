package com.xsw.track.util

import com.xsw.track.config.TrackConfig

class Log {

    static void i(String text) {
        if (!TrackConfig.isDebug()) {
            return
        }
        if (Utils.hasNull(text)) {
            return
        }
    }

}