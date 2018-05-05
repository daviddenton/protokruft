package org.protokruft

import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertTrue
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Ignore
import org.junit.Test

class PluginTest {
    @Test
    @Ignore
    fun test_plugin() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("protokruft")

        assertTrue(project.pluginManager.hasPlugin("protokruft"))

        assertNotNull(project.tasks.getByName("yourTask"))
    }
}