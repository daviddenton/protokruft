package org.protokruft

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class TestFileCreator {

    @Rule
    @JvmField
    val folder = TemporaryFolder()

    @Test
    fun testCreatesFileWithContent() {
        val tempFile = File.createTempFile("temp", ".tmp")
        val creator = FileCreator(tempFile)
        creator.create()
        assertTrue(tempFile.exists())
        assertEquals("HELLO FROM MY PLUGIN", tempFile.reader().readText())
    }

    @Test
    fun testCreatesFileIfParentDirMissing() {
        val tempFile = folder.newFile("testing.tmp")

        val creator = FileCreator(tempFile)
        creator.create()
        assertTrue(tempFile.exists())
        assertEquals("HELLO FROM MY PLUGIN", tempFile.reader().readText())
    }
}
