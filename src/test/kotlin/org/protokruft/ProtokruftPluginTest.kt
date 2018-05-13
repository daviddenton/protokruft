package org.protokruft

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

class ProtokruftPluginTest {

    @Test
    fun applyPlugin() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("protokruft")

        assertThat(project.pluginManager.hasPlugin("protokruft"), equalTo(true))
        assertThat(project.tasks.getByName("generateProtoDsl"), present())
    }
}