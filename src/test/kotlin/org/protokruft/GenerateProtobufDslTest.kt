package org.protokruft

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test
import java.io.StringWriter

class GenerateProtobufDslTest {
    @Test
    fun `generates expected output`() {
        val generated = GenerateProtobufDsl.generate(ScanClasspath("org.protokruft"), "name")

        fun check(i: Int) {
            val w = StringWriter()
            generated[i - 1].writeTo(w)
            println(w.toString())
            assertThat(w.toString(), equalTo(javaClass.getResourceAsStream("/expected$i.ktt").reader().readText()))
        }

        check(1)
        assertThat(generated.size, equalTo(1))
    }
}