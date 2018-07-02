package org.protokruft

import org.gradle.api.Project
import java.io.File

open class ProtokruftOptions(var messagesClassFile: String,
                             var servicesClassFile: String,
                             var packageNames: Set<String>?,
                             var outputDirectory: (Project) -> File) {
    constructor() : this("messageDsl", "servicessDsl", null, { File(it.buildDir, "/generated/source/proto/main/java") })
}