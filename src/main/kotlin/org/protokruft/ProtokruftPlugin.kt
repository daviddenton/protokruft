package org.protokruft


import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class ProtokruftPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.create(GenerateProtobufDslTask.NAME, GenerateProtobufDslTask::class.java).apply {
            group = "Protokruft"
            description = "Generate Kotlin DSLs for Protobuf messages"
            outputFile = File(project.buildDir, "foobartastic.txt")
        }
    }
}
