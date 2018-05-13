package org.protokruft

import com.google.protobuf.gradle.GenerateProtoTask
import com.squareup.kotlinpoet.ClassName
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import java.io.File

open class GenerateProtobufDslTask : DefaultTask() {
    var outputClassFile = "generated"
    val outputDirectory = "src/example/kotlin"

    @TaskAction
    fun action() = GenerateProtobufDsl.generate(GeneratedProtos(project), outputClassFile)
            .forEach { it.writeTo(File(outputDirectory)) }

    companion object {
        const val NAME = "generateProtobufDsl"
    }
}

fun GeneratedProtos(project: Project): TargetMessageClasses = {
    fun generatedFiles() =
            (project.getTasksByName("generateProto", false)
                    .first() as GenerateProtoTask)
                    .outputSourceDirectorySet.map { it }

    generatedFiles().flatMap {
        val input = it.readText()
        val pkg = Regex("package (.*);").find(input)!!.groupValues[1]
        val classes = Regex("public static (.*) parseFrom")
                .findAll(input).map { it.groupValues[1] }
                .distinct()
                .map { it.removePrefix("$pkg.") }
                .toList()

        classes.map { ClassName(pkg, it) }
    }
}



