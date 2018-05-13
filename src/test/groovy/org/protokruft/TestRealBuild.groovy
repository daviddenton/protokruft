package org.protokruft

import kotlin.jvm.JvmField
import org.apache.commons.io.FileUtils
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class TestRealBuild extends GroovyTestCase {
    @Rule
    @JvmField
    def root = new TemporaryFolder()
    File simpleProjectDir
    File configuredProjectDir

    def pluginClasspathResource = getClass().classLoader.findResource("plugin-classpath.txt")
    def pluginClasspath = pluginClasspathResource.readLines().collect { new File(it) }

    void setUp() {
        root.create()
        simpleProjectDir = root.newFolder("simple")
        configuredProjectDir = root.newFolder("configured")
        def protoDir = new File(simpleProjectDir, "src/main/proto")
        resourceTo("/example.proto", protoDir)
        resourceTo("/build.gradle", simpleProjectDir)
//        new File(simpleProjectDir, "build.gradle").write(new File(getClass().getResource("/simple.gradle").getFile()).text)
        new File(configuredProjectDir, "build.gradle").write(new File(getClass().getResource("/configured.gradle").getFile()).text)
    }

    private resourceTo(String s, File simpleProjectDir) {
        simpleProjectDir.mkdirs()
        FileUtils.copyFileToDirectory(new File(getClass().getResource(s).getFile()), simpleProjectDir)
    }

    void tearDown() {
        setUp()
    }

    void testMyTask() {
        def result = GradleRunner.create()
                .withProjectDir(simpleProjectDir)
                .withPluginClasspath(pluginClasspath)
                .withArguments("mytask")
                .build()

        assertEquals(SUCCESS, result.task(":mytask").getOutcome())
        assertTrue((new File(simpleProjectDir, "build/myfile.txt")).exists())
    }

    void testGenerateProto() {
        def result = GradleRunner.create()
                .withProjectDir(simpleProjectDir)
                .withPluginClasspath(pluginClasspath)
                .withArguments("generateProto", "--info")
                .build()
        println result.output
    }

    void testMyOtherTask() {
        def result = GradleRunner.create()
                .withProjectDir(simpleProjectDir)
                .withPluginClasspath(pluginClasspath)
                .withArguments("myothertask")
                .build()

        assertEquals(SUCCESS, result.task(":myothertask").getOutcome())
        assertTrue((new File(simpleProjectDir, "build/otherfile.txt")).exists())
    }

    void testConfiguration() {
        def testFile = new File(configuredProjectDir, "build/myfile.txt")

        def result = GradleRunner.create()
                .withProjectDir(configuredProjectDir)
                .withPluginClasspath(pluginClasspath)
                .withArguments("mytask")
                .build()

        assertEquals(SUCCESS, result.task(":mytask").getOutcome())
        assertTrue(testFile.exists())
        assertEquals("CONFIGURED", testFile.text)
    }
}
