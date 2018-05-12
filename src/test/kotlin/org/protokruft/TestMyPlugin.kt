package org.protokruft

import junit.framework.Assert.assertEquals
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import java.io.File

class TestMyPlugin {
    @Test
    fun testMyTask() {
        val project = ProjectBuilder.builder().withName("hello-world").build()
        project.pluginManager.apply(ProtokruftPlugin::class.java)

        assertEquals(File(project.buildDir, "myfile.txt"), (project.tasks.getByName("mytask") as ProtokruftTask).outputFile)
    }

    @Test
    fun testMyOtherTask() {
        val project = ProjectBuilder.builder().withName("hello-world").build()
        project.pluginManager.apply(ProtokruftPlugin::class.java)

        assertEquals(File(project.buildDir, "otherfile.txt"), (project.tasks.getByName("myothertask") as ProtokruftTask).outputFile)
    }

    @Test
    fun testHasExtension() {
        val project = ProjectBuilder.builder().withName("hello-world").build()
        project.pluginManager.apply(ProtokruftPlugin::class.java)

        assertEquals("¯\\_(ツ)_/¯", project.extensions.findByType(ProtokruftPluginExtension::class.java)!!.fileContent)
    }
}