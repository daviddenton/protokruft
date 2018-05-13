package org.protokruft

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class GenerateProtobufDslTask : DefaultTask() {
    var outputFile = File(project.buildDir, "foobartastic.txt")

    @TaskAction
    fun action() {
        outputFile.parentFile.mkdirs()
        outputFile.createNewFile()
    }

    companion object {
        val NAME = "generateProtobufDsl"
    }
}