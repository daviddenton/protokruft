package org.protokruft

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class GenerateProtobufDslPlugin : DefaultTask() {
    var message: String? = null
    var recipient: String? = null

    @TaskAction
    internal fun generateClasses() {
        System.out.printf("%s, %s!\n", message, recipient)
    }
}