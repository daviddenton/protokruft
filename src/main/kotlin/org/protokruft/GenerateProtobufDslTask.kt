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
    fun action() = GenerateProtobufDsl.generate(GeneratedProtos(project, options.packageNames), options.outputClassFile)
            .forEach {
                val directory = options.outputDirectory(project)
                project.logger.debug("Protokruft: writing ${it.packageName}.${it.name}.kt to ${directory.absolutePath}")
                it.writeTo(directory)
            }

    companion object {
        const val NAME = "generateProtobufDsl"
    }
}

fun GeneratedProtos(project: Project, packageNames: Set<String>?): TargetMessageClasses = {

    fun toClassName(pkg: String): (String) -> ClassName = {
        it.split('.').reversed()
                .let { ClassName(pkg, it.last(), *it.dropLast(1).toTypedArray()) }
    }

    fun generatedFiles(): List<File> =
            (project.getTasksByName("generateProto", false)
                    .first() as GenerateProtoTask)
                    .outputSourceDirectorySet.map { it }
                    .also {
                        project.logger.debug("Protokruft: found files: " + it.toString())
                    }

    fun findAllClassesIn(input: String, pkg: String) = Regex("public static (.*) parseFrom")
            .findAll(input).map { it.groupValues[1] }
            .distinct()
            .toList()
            .map { it.removePrefix("$pkg.") }

    project.logger.debug("Protokruft: filtering classes to packages: " + (packageNames?.toString() ?: "*"))

    generatedFiles().flatMap {

        val pkg = Regex("package (.*);").find(it.readText())!!.groupValues[1]

        findAllClassesIn(it.readText(), pkg).map(toClassName(pkg))
                .filter { clz ->
                    packageNames?.any { clz.packageName().startsWith(it) } ?: true
                }
                .also {
                    project.logger.debug("Protokruft: found classes to generate: " + it.toString())
                }
    }
}
