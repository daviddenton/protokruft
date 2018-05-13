package org.protokruft

import org.gradle.api.Project
import java.io.File

open class ProtokruftOptions(var outputClassFile: String, var outputDirectory: (Project) -> File) {
    constructor() : this("generated", { File(it.buildDir, "/generated/source/proto/main/java") })
}