package org.protokruft

import org.gradle.api.Project
import java.io.File

open class ProtokruftOptions(var outputClassFile: String,
                             var packageNames: Set<String>?,
                             var outputDirectory: (Project) -> File) {
    constructor() : this("messageDsl", null, { File(it.buildDir, "/generated/source/proto/main/java") })
}