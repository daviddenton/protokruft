package org.protokruft

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test
import org.protokruft.TargetMessageClasses.Companion.ScanClasspath
import java.io.StringWriter

class DecruftinateTest {
    @Test
    fun `generates expected output`() {
        val decruftinate = GenerateProtobufDsl.generate(ScanClasspath("org.protokruft.example"), "name")

        fun check(i: Int) {
            val w = StringWriter()
            decruftinate[i-1].writeTo(w)
            assertThat(w.toString(), equalTo(javaClass.getResourceAsStream("/expected$i.ktt").reader().readText()))
        }

        assertThat(decruftinate.size, equalTo(2))
        check(1)
        check(2)
    }
}