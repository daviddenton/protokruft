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
            assertThat(w.toString(), equalTo(javaClass.getResourceAsStream("/expected$i.ktt").reader().readText()))
        }

        assertThat(generated.size, equalTo(3))
        check(1)
        check(2)
        check(3)
    }
}