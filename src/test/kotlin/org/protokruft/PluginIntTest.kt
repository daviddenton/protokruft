package org.protokruft

import org.apache.commons.io.FileUtils
import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class PluginIntTest {

    @Rule
    @JvmField
    val testProjectDir = TemporaryFolder()
    val ou = File("/tmp/protokruft")

    @Before
    fun setUpProject() {
        ou.delete()
        resourceTo("/build.gradle", ou)
        resourceTo("/example.proto", File(ou, "src/main/proto"))
    }

    @Test
    @Ignore
    fun test() {
        val result = GradleRunner.create()
                .withProjectDir(ou)
                .withPluginClasspath()
                .withArguments("clean", "generateProto", "generateProtoDsl", "--info")
                .build()
        println(result.output)
    }

    private fun resourceTo(s: String, simpleProjectDir: File) {
        simpleProjectDir.mkdirs()
        FileUtils.copyFileToDirectory(File(this.javaClass.getResource(s).file), simpleProjectDir)
    }
}
