package org.protokruft

import com.google.protobuf.gradle.GenerateProtoTask
import com.squareup.kotlinpoet.ClassName
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import java.io.File

open class GenerateProtobufDslTask : DefaultTask() {
    var options: ProtokruftOptions = ProtokruftOptions()

    @TaskAction
    fun action() = GenerateProtobufDsl.generate(GeneratedProtos(project), options.outputClassFile)
            .forEach {
                val directory = options.outputDirectory(project)
                project.logger.debug("Protokruft: writing ${it.name}.kt to ${directory.absolutePath}")
                it.writeTo(directory) }

    companion object {
        const val NAME = "generateProtobufDsl"
    }
}

fun GeneratedProtos(project: Project): TargetMessageClasses = {
    fun generatedFiles(): List<File> =
            (project.getTasksByName("generateProto", false)
                    .first() as GenerateProtoTask)
                    .outputSourceDirectorySet.map { it }

    generatedFiles().flatMap {
        val input = it.readText()
        val pkg = Regex("package (.*);").find(input)!!.groupValues[1]
        val classes = Regex("public static (.*) parseFrom")
                .findAll(input).map { it.groupValues[1] }
                .distinct()
                .toList()
                .map { it.removePrefix("$pkg.") }

        classes.map {
            val parts = it.split('.').reversed()
            ClassName(pkg, parts.last(), *parts.dropLast(1).toTypedArray())
        }.also {
            project.logger.debug("Protokruft: found classes to generate: " + it.toString())
        }
    }
}