package org.protokruft


import org.gradle.api.Plugin
import org.gradle.api.Project

class ProtokruftPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.add("protokruft", ProtokruftOptions::class.java)

        project.tasks.create(GenerateProtobufDslTask.NAME, GenerateProtobufDslTask::class.java).apply {
            group = "Protokruft"
            project.extensions.findByType(ProtokruftOptions::class.java)?.also {
                options = it
            }
            description = "Generate Kotlin DSLs for Protobuf messages"
        }
    }
}

