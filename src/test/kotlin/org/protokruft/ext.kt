package org.protokruft

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.squareup.kotlinpoet.FileSpec
import java.io.StringWriter

fun Any.check(i: Int, list: List<FileSpec>, root: String) {
    val w = StringWriter()
    list[i - 1].writeTo(w)
    assertThat(w.toString(), equalTo(javaClass.getResourceAsStream("/$root$i.ktt").reader().readText()))
}