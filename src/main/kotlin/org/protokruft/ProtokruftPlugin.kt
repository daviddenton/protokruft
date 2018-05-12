package org.protokruft


import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class ProtokruftPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        project.extensions.add("protokruft", ProtokruftPluginExtension())

        project.tasks.create("mytask", ProtokruftTask::class.java).apply {
            group = "MyPlugin"
            description = "Create myfile.txt in the build directory"
        }

        project.tasks.create("myothertask", ProtokruftTask::class.java).apply {
            group = "MyPlugin"
            description = "Create otherfile.txt in the build directory"
            outputFile = File(project.buildDir, "otherfile2.txt")
        }

        project.afterEvaluate {
            print(project.extensions.getByType(ProtokruftPluginExtension::class.java).fileContent)
        }
    }
}
