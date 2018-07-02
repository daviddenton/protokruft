package org.protokruft

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.grpc.stub.AbstractStub
import org.junit.Test

class GenerateProtobufServiceDslTest {
    @Test
    fun `generates expected output for multiple packages`() {
        val generated = GenerateProtobufServiceDsl.generate(ScanClasspathFor("org.protokruft", AbstractStub::class.java), "name")

        assertThat(generated.size, equalTo(3))
//        check(1, generated, "expectedService")
//        check(2, generated, "expectedService")
//        check(3, generated, "expectedService")
    }
}