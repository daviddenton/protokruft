package org.protokruft

import org.gradle.api.Project
import java.io.File

open class ProtokruftOptions(var messagesClassFile: String = "messageDsl",
                             var servicesClassFile: String = "serviceDsl",
                             var messageDslPrefix: String = "new",
                             var serviceDslSuffix: String = "",
                             var packageNames: Set<String>? = null,
                             var markerInterface: String? = null,
                             var outputDirectory: (Project) -> File = { File(it.buildDir, "/generated/source/proto/main/java") })