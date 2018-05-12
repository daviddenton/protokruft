package org.protokruft

import java.io.File

class FileCreator(val outputFile: File) {
    fun create() {
        this.outputFile.parentFile.mkdirs()
        this.outputFile.createNewFile()
        this.outputFile.writeText("HELLO FROM MY PLUGIN")
    }
}