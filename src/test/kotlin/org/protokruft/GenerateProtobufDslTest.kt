package org.protokruft

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import org.junit.Test
import java.io.StringWriter

class GenerateProtobufDslTest {
    fun check(i: Int, list: List<FileSpec>, root: String) {
        val w = StringWriter()
        list[i - 1].writeTo(w)
        assertThat(w.toString(), equalTo(javaClass.getResourceAsStream("/$root$i.ktt").reader().readText()))
    }

    @Test
    fun `generates expected output for multiple packages`() {
        val generated = GenerateProtobufDsl.generate(ScanClasspath("org.protokruft"), "name")

        assertThat(generated.size, equalTo(3))
        check(1, generated, "expected")
        check(2, generated, "expected")
        check(3, generated, "expected")
    }

    @Test
    fun `generates expected output for nested and bare types`() {
        val generated = GenerateProtobufDsl.generate({
            listOf(
                    ClassName("examplepackage", "Timestamp"),
                    ClassName("examplepackage", "Example1"),
                    ClassName("examplepackage", "Example1.Bob"),
                    ClassName("examplepackage", "Example1.Bob"),
                    ClassName("examplepackage", "Example1.Bob.Bill")
            )
        }, "name")

        assertThat(generated.size, equalTo(1))
        check(1, generated, "extended")
    }
}