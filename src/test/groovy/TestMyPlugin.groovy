import org.protokruft.ProtokruftPluginExtension
import org.protokruft.ProtokruftTask
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.protokruft.ProtokruftPlugin

class TestMyPlugin extends GroovyTestCase {
    void testDealWithIt() {
        Project project = ProjectBuilder.builder().withName("hello-world").build()
        project.pluginManager.apply ProtokruftPlugin

        assertNotNull(project.tasks.dealwithit)
    }

    void testMyTask() {
        Project project = ProjectBuilder.builder().withName("hello-world").build()
        project.pluginManager.apply ProtokruftPlugin

        assertTrue(project.tasks.mytask instanceof ProtokruftTask)
        assertEquals(new File(project.buildDir, "myfile.txt"), project.tasks.mytask.outputFile)
    }

    void testMyOtherTask() {
        Project project = ProjectBuilder.builder().withName("hello-world").build()
        project.pluginManager.apply ProtokruftPlugin

        assertTrue(project.tasks.myothertask instanceof ProtokruftTask)
        assertEquals(new File(project.buildDir, "otherfile.txt"), project.tasks.myothertask.outputFile)
    }

    void testHasExtension() {
        Project project = ProjectBuilder.builder().withName("hello-world").build()
        project.pluginManager.apply ProtokruftPlugin
        assertTrue(project.extensions.protokruft instanceof ProtokruftPluginExtension)
        assertEquals("¯\\_(ツ)_/¯", project.extensions.protokruft.fileContent)
    }
}
