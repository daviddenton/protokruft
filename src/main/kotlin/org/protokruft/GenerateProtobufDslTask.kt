package org.protokruft

import com.google.protobuf.gradle.GenerateProtoTask
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ClassName.Companion.bestGuess
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

open class GenerateProtobufDslTask : DefaultTask() {
    var options: ProtokruftOptions = ProtokruftOptions()

    @TaskAction
    fun action() {
        val messages = GenerateProtobufMessageDsl.generate(GeneratedMessageProtos(project, options.packageNames), options.messagesClassFile)
        val services = GenerateProtobufServiceDsl.generate(GeneratedServiceProtos(project, options.packageNames), options.servicesClassFile)
        (messages + services)
                .forEach {
                    val directory = options.outputDirectory(project)
                    project.logger.info("Protokruft: writing ${it.packageName}.${it.name}.kt to ${directory.absolutePath}")
                    it.writeTo(directory)
                }
    }

    companion object {
        const val NAME = "generateProtobufDsl"
    }
}

fun GeneratedMessageProtos(project: Project, packageNames: Set<String>?): TargetMessageClasses = {
    project.logger.info("Protokruft: filtering Message classes to packages: " + (packageNames?.toString() ?: "*"))

    project.generatedFiles().mapNotNull {
        project.logger.info("Protokruft: processing: ${it.absolutePath}")
        val input = it.readText()

        Regex("package (.*);").find(input)?.let { it.groupValues[1] }?.let {
            Regex("public static (.*) parseFrom").findAllClassesIn(input, it).limitToPackages(project, it, packageNames)
        }
    }.flatten()
}

fun GeneratedServiceProtos(project: Project, packageNames: Set<String>?): TargetServiceClasses = {
    project.logger.info("Protokruft: filtering Message classes to packages: " + (packageNames?.toString() ?: "*"))

    project.generatedFiles().mapNotNull { file ->
        project.logger.info("Protokruft: processing: ${file.absolutePath}")
        val input = file.readText()
        Regex("package (.*);").find(input)?.let { it.groupValues[1] }?.let {
            GrpcService(ClassName(it, (file.nameWithoutExtension)),
                    Regex("""public static io.grpc.MethodDescriptor<(.*),\s+(.*)>\s+get(.*)\(\)""").findAll(input).map {
                        GrpcMethod(it.groupValues[3].replace("get", "").decapitalize(), listOf(bestGuess(it.groupValues[3])), bestGuess(it.groupValues[2]))
                    }.toList()
            )
        }
    }.filter {
        println(it)
        packageNames?.contains(it.className.packageName()) ?: true
    }
}

private fun List<String>.limitToPackages(project: Project, pkg: String, packageNames: Set<String>?) =
        map(toClassNameFn(pkg))
                .filter { clz -> packageNames?.any { clz.packageName().startsWith(it) } ?: true }
                .also { project.logger.info("Protokruft: found classes to generate: " + it.toString()) }

private fun Project.generatedFiles() =
        (getTasksByName("generateProto", false)
                .first() as GenerateProtoTask)
                .outputSourceDirectorySet.map { it }
                .filter { it.name.endsWith(".java") }

private fun Regex.findAllClassesIn(input: String, pkg: String): List<String> {
    return findAll(input).map {
        it.groupValues[1]
    }
            .distinct()
            .map { it.removePrefix("$pkg.") }
            .toList()
}

fun toClassNameFn(pkg: String): (String) -> ClassName = {
    it.split('.').reversed()
            .let { ClassName(pkg, it.last(), *it.dropLast(1).reversed().toTypedArray()) }
}
