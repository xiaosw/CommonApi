package com.xsw.track.config

import com.xsw.track.util.Log

class TrackConfig {

    def static final TAG_PREFIX = "Track-"
    def static final TRACK_MANAGER_NAME = "com/xsw/track/TrackHelper"

    private def static isDebug = false
    private def static isTrack = false
    private def static isAddBuildTaskListener = false
    private def static Set<String> mTrackTargetPackages = []

    static void setDebug(boolean debug) {
        isDebug = debug
    }

    static boolean isDebug() {
        return isDebug
    }

    static void setTrack(boolean track) {
        isTrack = track
    }

    static boolean isTrack() {
        return isTrack
    }

    static void setAddBuildTaskListener(boolean isAddBuildTaskListener) {
        this.isAddBuildTaskListener = isAddBuildTaskListener
    }

    static boolean isAddBuildTaskListener() {
        return isAddBuildTaskListener
    }

    static void addTrackTargetPackage(String trackTargetPackage) {
        if (null == trackTargetPackage) {
            return
        }
        mTrackTargetPackages.add(trackTargetPackage)
    }

    static void addTrackTargetPackages(Set<String> trackTargetPackages) {
        if (null == trackTargetPackages) {
            return
        }
        mTrackTargetPackages.addAll(trackTargetPackages)
    }

    static Set<String> getTrackTargetPackages() {
        return mTrackTargetPackages
    }

    static void printConfig() {
        Log.i("TrackConfig{isDebug = $isDebug, isTrack = $isTrack, isAddBuildListener = $isAddBuildTaskListener}")
    }
}