package org.protokruft

import org.apache.commons.io.FileUtils
import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.protokruft.GenerateProtobufDslTask.Companion.NAME
import java.io.File

class ProtokruftPluginRealBuildTest {

    @Rule
    @JvmField
    val testProjectDir = TemporaryFolder()
    val root = File("/tmp/protokruft")
    lateinit var pluginClasspath: List<File>

    @Before
    fun setUpProject() {
        testProjectDir.create()
        root.delete()
        resourceTo("/build.gradle", root)
        resourceTo("/example.proto", File(root, "src/main/proto"))
        val pluginClasspathResource = File(javaClass.classLoader.getResource("plugin-classpath.txt").file)
        pluginClasspath = pluginClasspathResource.reader().readLines().map { File(it) }
    }

    @Test
    fun generatesOutputForProtobufFiles() {
        val result = GradleRunner.create()
                .withProjectDir(root)
                .withPluginClasspath(pluginClasspath)
                .withArguments("clean", "generateProto", NAME, "--info")
                .build()
        println(result.output)
    }

    private fun resourceTo(s: String, simpleProjectDir: File) {
        simpleProjectDir.mkdirs()
        FileUtils.copyFileToDirectory(File(this.javaClass.getResource(s).file), simpleProjectDir)
    }
}
