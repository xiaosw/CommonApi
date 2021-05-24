package com.xsw.track

import com.android.build.gradle.BaseExtension
import com.xsw.track.config.TrackConfig
import com.xsw.track.extension.TrackExtension
import com.xsw.track.global.TrackGlobal
import com.xsw.track.listener.TrackListener
import com.xsw.track.transform.TrackTransform
import com.xsw.track.util.Log
import org.gradle.api.Plugin
import org.gradle.api.Project

class TrackPlugin implements Plugin<Project> {

    @Override
    void apply(Project target) {
        def isApp = target.plugins.hasPlugin("com.android.application")
        if (!isApp) {
            println("------------------------ track plugin must config in app model -----------------------")
            return
        }
        def extensions = target.extensions
        // add track extension
        extensions.add("trackConfig", TrackExtension)
        // init copy temp dir
        final def transformTempDir = new File(target.buildDir, "TransformTempDir")
        if (!transformTempDir.exists()) {
            transformTempDir.mkdir()
        }
        TrackGlobal.global.TransformTempDir = transformTempDir

        // register transform
        extensions.getByType(BaseExtension).registerTransform(new TrackTransform(target))
        target.afterEvaluate() {
            TrackConfig.setDebug(target.trackConfig.isDebug)
            TrackConfig.setTrack(target.trackConfig.isTrack)
            TrackConfig.setAddBuildTaskListener(target.trackConfig.isAddBuildTaskListener)
            TrackConfig.addTrackTargetPackages(target.trackConfig.trackTargetPackages)
            TrackConfig.printConfig()
            Log.help()
            if (TrackConfig.isAddBuildTaskListener()) {
                target.gradle.addListener(new TrackListener())
            }
        }
    }

}