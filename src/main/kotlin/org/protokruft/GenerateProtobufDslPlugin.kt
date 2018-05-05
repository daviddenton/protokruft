package org.protokruft

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

class GenerateProtobufDslPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.tasks.create(TASK_NAME, ATask::class.java)
    }

    companion object {
        internal val TASK_NAME = "yourTask"
    }
}

open class ATask : DefaultTask() {
    @TaskAction
    fun generateClasses() {
        System.out.printf("HELLO!")
    }
}