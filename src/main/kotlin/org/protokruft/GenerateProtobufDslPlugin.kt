package org.protokruft

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

class GenerateProtobufDslPlugin : Plugin<Project> {
    init {
        println("hello!!!!!!")
    }
    override fun apply(target: Project) {
        println(target.pluginManager.findPlugin("com.google.protobuf"))
        target.tasks.create("generateProtoDsl", GenerateProtoDsl::class.java)
    }
}

open class GenerateProtoDsl : DefaultTask() {
    init {
        println("creating task")
    }
    @TaskAction
    fun generateClasses() {
        System.out.printf("HELLO!")
    }
}