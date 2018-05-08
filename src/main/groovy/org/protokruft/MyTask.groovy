package org.protokruft

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class MyTask extends DefaultTask {
    File outputFile = new File(project.buildDir, "myfile.txt")

    @TaskAction
    def action() {
        outputFile.parentFile.mkdirs()
        outputFile.createNewFile()
        outputFile.text = project.extensions.protokruft.fileContent
    }
}
