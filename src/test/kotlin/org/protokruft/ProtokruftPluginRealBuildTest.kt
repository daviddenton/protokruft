package org.protokruft

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.containsSubstring
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
        resourceTo("/example2.proto", File(testProjectDir.root, "src/main/proto"))
        resourceTo("/example3.proto", File(testProjectDir.root, "src/main/proto"))
        val pluginClasspathResource = File(javaClass.classLoader.getResource("plugin-classpath.txt").file)
        pluginClasspath = pluginClasspathResource.reader().readLines().map { File(it) }
    }

    @Test
    fun generatesOutputForProtobufFiles() {
        val result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath(pluginClasspath)
                .withArguments("clean", "generateProto", NAME, "--info")
                .build()

        println(result.output)

        val expectedMessage = File(testProjectDir.root, "build/generated/source/proto/main/java/org/protokruft/example3/customMessage.kt").readText()
        val excludedMessage = File(testProjectDir.root, "build/generated/source/proto/main/java/org/protokruft/example2/custom.kt")
        val expectedService = File(testProjectDir.root, "build/generated/source/proto/main/java/org/protokruft/example3/customService.kt").readText()
        val excludedService = File(testProjectDir.root, "build/generated/source/proto/main/java/org/protokruft/example2/customService.kt")
        assertThat(expectedMessage, equalTo(javaClass.getResourceAsStream("/expectedMessage3.ktt").reader().readText()))
        assertThat("excluded package was generated " + excludedMessage.absolutePath, excludedMessage.exists(), equalTo(false))
        assertThat(expectedService, equalTo(javaClass.getResourceAsStream("/expectedService3.ktt").reader().readText()))
        assertThat("excluded package was generated " + excludedService.absolutePath, excludedService.exists(), equalTo(false))
    }

    @Test
    fun generatesOutputForProtobufFilesIncrementally() {
        GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withPluginClasspath(pluginClasspath)
            .withArguments("clean", "generateProto", NAME, "--info")
            .build()

        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withPluginClasspath(pluginClasspath)
            .withArguments("generateProto", NAME, "--info")
            .build()

        println(result.output)

        assertThat(result.output, containsSubstring("Task :generateProtobufDsl UP-TO-DATE"))
    }

    private fun resourceTo(file: String, dir: File) {
        dir.mkdirs()
        FileUtils.copyFileToDirectory(File(javaClass.getResource(file).file), dir)
    }
}
