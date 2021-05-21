package com.xsw.track

import com.xsw.track.config.TrackConfig
import org.gradle.api.Plugin
import org.gradle.api.Project

class TrackPlugin implements Plugin<Project> {

    @Override
    void apply(Project target) {
        target.extensions.add("trackConfig", com.xsw.track.extension.TrackExtension)
        target.afterEvaluate() {
            boolean isDebug = target.trackConfig.isDebug
            println("------------------- xsw plugin $isDebug --------------------")
            TrackConfig.setDebug(isDebug)
        }
    }

}