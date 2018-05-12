package org.protokruft

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class ProtokruftTask : DefaultTask() {
    var outputFile = File(project.buildDir, "myfile.txt")

    @TaskAction
    fun action() {
        outputFile.parentFile.mkdirs()
        outputFile.createNewFile()
        outputFile.writeText(project.extensions.getByType(ProtokruftPluginExtension::class.java).fileContent)
    }
}