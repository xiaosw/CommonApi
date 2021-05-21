package com.xsw.track.config

class TrackConfig {

    private def static isDebug = false

    static void setDebug(boolean debug) {
        isDebug = debug
    }

    static boolean isDebug() {
        return isDebug
    }

}