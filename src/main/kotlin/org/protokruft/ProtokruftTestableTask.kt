package org.protokruft

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class ProtokruftTestableTask : DefaultTask() {
    var outputFile = File(project.buildDir, "myfile.txt")

    @TaskAction
    fun action() {
        val creator = FileCreator(outputFile)
        creator.create()
    }
}