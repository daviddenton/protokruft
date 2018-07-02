package org.protokruft

import com.google.protobuf.gradle.GenerateProtoTask
import com.squareup.kotlinpoet.ClassName
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

open class GenerateProtobufDslTask : DefaultTask() {
    var options: ProtokruftOptions = ProtokruftOptions()

    @TaskAction
    fun action() {
        val messages = GenerateProtobufMessageDsl.generate(GeneratedMessageProtos(project, options.packageNames), options.outputClassFile)
        val services = GenerateProtobufServiceDsl.generate(GeneratedServiceProtos(project, options.packageNames), options.outputClassFile)
        (messages + services)
                .forEach {
                    val directory = options.outputDirectory(project)
                    project.logger.debug("Protokruft: writing ${it.packageName}.${it.name}.kt to ${directory.absolutePath}")
                    it.writeTo(directory)
                }
    }

    companion object {
        const val NAME = "generateProtobufDsl"
    }
}

fun GeneratedMessageProtos(project: Project, packageNames: Set<String>?): TargetMessageClasses = {

    project.logger.debug("Protokruft: filtering classes to packages: " + (packageNames?.toString() ?: "*"))

    project.generatedFiles()
            .flatMap { file ->
                project.logger.debug("Protokruft: processing: ${file.absolutePath}")
                val input = file.readText()

                Regex("package (.*);").find(input)?.let { it.groupValues[1] }?.let { pkg ->
                    project.findAllClassesIn(Regex("public static (.*) parseFrom"), input, pkg).limitToPackages(project, pkg, packageNames)
                } ?: emptyList<ClassName>().also {
                    project.logger.warn("Protokruft: no package statement found in: ${file.absolutePath}")
                }
            }
}

fun GeneratedServiceProtos(project: Project, packageNames: Set<String>?): TargetServiceClasses = {

    project.logger.info("Protokruft: filtering classes to packages: " + (packageNames?.toString() ?: "*"))

    project.generatedFiles()
            .flatMap { file ->
                project.logger.info("Protokruft: processing: ${file.absolutePath}")
                val input = file.readText()

                Regex("package (.*);").find(input)?.let { it.groupValues[1] }?.let { pkg ->
                    project.findAllClassesIn(Regex("public static (.*) parseFrom"), input, pkg).limitToPackages(project, pkg, packageNames)
                } ?: emptyList<ClassName>().also {
                    project.logger.warn("Protokruft: no package statement found in: ${file.absolutePath}")
                }
            }
}

private fun List<String>.limitToPackages(project: Project, pkg: String, packageNames: Set<String>?) =
        map(toClassNameFn(pkg))
                .filter { clz -> packageNames?.any { clz.packageName().startsWith(it) } ?: true }
                .also { project.logger.debug("Protokruft: found classes to generate: " + it.toString()) }

private fun Project.generatedFiles() =
        (getTasksByName("generateProto", false)
                .first() as GenerateProtoTask)
                .outputSourceDirectorySet.map { it }
                .filter { it.name.endsWith(".java") }
                .also {
                    project.logger.debug("Protokruft: found files: " + it.toString())
                }

private fun Project.findAllClassesIn(regex: Regex, input: String, pkg: String) = regex
        .findAll(input).map { it.groupValues[1] }
        .distinct()
        .map { it.removePrefix("$pkg.") }
        .toList()
        .also {
            logger.info("Protokruft: found classes: ${it.joinToString(", ")}")
        }

fun toClassNameFn(pkg: String): (String) -> ClassName = {
    it.split('.').reversed()
            .let { ClassName(pkg, it.last(), *it.dropLast(1).reversed().toTypedArray()) }
}
