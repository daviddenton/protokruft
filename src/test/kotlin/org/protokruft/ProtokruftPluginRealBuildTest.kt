package org.protokruft

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
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
    lateinit var pluginClasspath: List<File>

    @Before
    fun setUpProject() {
        testProjectDir.create()
        resourceTo("/build.gradle", testProjectDir.root)
        resourceTo("/example1.proto", File(testProjectDir.root, "src/main/proto"))
        val pluginClasspathResource = File(javaClass.classLoader.getResource("plugin-classpath.txt").file)
        pluginClasspath = pluginClasspathResource.reader().readLines().map { File(it) }
    }

    @Test
    fun generatesOutputForProtobufFiles() {
        val result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath(pluginClasspath)
                .withArguments("clean", "generateProto", NAME, "--debug")
                .build()

        val generated = File(testProjectDir.root, "build/generated/source/proto/main/java/org/protokruft/example1/custom.kt").readText()
        assertThat(generated, equalTo(javaClass.getResourceAsStream("/expected1.ktt").reader().readText()))
        println(result.output)
    }

    private fun resourceTo(file: String, dir: File) {
        dir.mkdirs()
        FileUtils.copyFileToDirectory(File(javaClass.getResource(file).file), dir)
    }
}
