package org.protokruft

import com.google.protobuf.GeneratedMessageV3
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.squareup.kotlinpoet.ClassName
import org.junit.Test

class GenerateProtobufMessageDslTest {
    @Test
    fun `generates expected output for multiple packages`() {
        val generated = GenerateProtobufMessageDsl.generate(ScanClasspathFor("org.protokruft", GeneratedMessageV3::class.java), "name", "prefix")

        assertThat(generated.size, equalTo(3))
        check(1, generated, "expectedMessage")
        check(2, generated, "expectedMessage")
        check(3, generated, "expectedMessage")
    }

    @Test
    fun `generates expected output for nested and bare types`() {
        val generated = GenerateProtobufMessageDsl.generate({
            listOf(
                    ClassName("examplepackage", "Timestamp"),
                    ClassName("examplepackage", "Example1"),
                    ClassName("examplepackage", "Example1.Bob"),
                    ClassName("examplepackage", "Example1.Bob"),
                    ClassName("examplepackage", "Example1.Bob.Bill")
            )
        }, "name", "prefix")

        assertThat(generated.size, equalTo(1))
        check(1, generated, "extendedMessage")
    }
}