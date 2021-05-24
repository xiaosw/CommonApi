package com.xsw.track.listener

import com.xsw.track.config.TrackConfig
import com.xsw.track.util.Log
import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.TaskState

class TrackListener implements TaskExecutionListener, BuildListener {

    private static final TAG = "${TrackConfig.TAG_PREFIX}TrackListener --->>> "

    @Override
    void beforeExecute(Task task) {
        Log.i("$TAG pre execute task: ${task.name}")
    }

    @Override
    void afterExecute(Task task, TaskState state) {
        Log.i("$TAG task execute complete: ${task.name}")
    }

    @Override
    void buildStarted(Gradle gradle) {
        Log.i("$TAG buildStarted")
    }

    @Override
    void settingsEvaluated(Settings settings) {
        Log.i("$TAG settingsEvaluated")
    }

    @Override
    void projectsLoaded(Gradle gradle) {
        Log.i("$TAG projectsLoaded")
    }

    @Override
    void projectsEvaluated(Gradle gradle) {
        Log.i("$TAG projectsEvaluated")
    }

    @Override
    void buildFinished(BuildResult result) {
        Log.i("$TAG buildFinished")
    }

}