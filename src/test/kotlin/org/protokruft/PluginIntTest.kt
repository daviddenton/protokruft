package org.protokruft

import org.gradle.testkit.runner.GradleRunner
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class PluginIntTest {
    @Rule
    @JvmField
    val testProjectDir = TemporaryFolder()

    private fun setUpProject() {
        javaClass.getResourceAsStream("/build.gradle")
                .copyTo(testProjectDir.newFile("build.gradle").outputStream())
    }

    @Test
    @Ignore
    fun test() {
        setUpProject()

        GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withArguments("generateProtoDsl")
                .build()
    }
}