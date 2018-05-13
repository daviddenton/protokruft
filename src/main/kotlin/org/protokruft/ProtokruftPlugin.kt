package org.protokruft


import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class ProtokruftPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.create("generateProtoDsl", ProtokruftTask::class.java).apply {
            group = "MyPlugin"
            description = "Create otherfile.txt in the build directory"
            outputFile = File(project.buildDir, "otherfile.txt")
        }
    }
}
