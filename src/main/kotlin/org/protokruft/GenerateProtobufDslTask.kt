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

    project.generatedFiles().mapNotNull {
        project.logger.debug("Protokruft: processing: ${it.absolutePath}")
        val input = it.readText()

        Regex("package (.*);").find(input)?.let { it.groupValues[1] }?.let {
            Regex("public static (.*) parseFrom").findAllClassesIn(input, it).limitToPackages(project, it, packageNames)
        }
    }.flatten()
}

fun GeneratedServiceProtos(project: Project, packageNames: Set<String>?): TargetServiceClasses = {

    project.logger.info("Protokruft: filtering classes to packages: " + (packageNames?.toString() ?: "*"))

    project.generatedFiles().mapNotNull {
        project.logger.info("Protokruft: processing: ${it.absolutePath}")
        val input = it.readText()

        Regex("package (.*);").find(input)?.let { it.groupValues[1] }?.let {
            Regex("public static (.*) parseFrom").findAllClassesIn(input, it).limitToPackages(project, it, packageNames)
        }
    }.flatten()
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

private fun Regex.findAllClassesIn(input: String, pkg: String) =
        findAll(input).map { it.groupValues[1] }
                .distinct()
                .map { it.removePrefix("$pkg.") }
                .toList()

fun toClassNameFn(pkg: String): (String) -> ClassName = {
    it.split('.').reversed()
            .let { ClassName(pkg, it.last(), *it.dropLast(1).reversed().toTypedArray()) }
}
