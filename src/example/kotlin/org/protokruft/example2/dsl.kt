package org.protokruft.example2

import kotlin.Unit

object newAddress {
    private fun new(): Example2.Address.Builder = org.protokruft.example2.Example2.Address.newBuilder()

    operator fun invoke(fn: Example2.Address.Builder.() -> Unit): Example2.Address = new().apply(fn).build()

    fun apply(fn: Example2.Address.Builder.() -> Unit): Example2.Address = invoke(fn)

    fun let(fn: (Example2.Address.Builder) -> Unit): Example2.Address = new().apply(fn).build()
}

object newPerson {
    private fun new(): Example2.Person.Builder = org.protokruft.example2.Example2.Person.newBuilder()

    operator fun invoke(fn: Example2.Person.Builder.() -> Unit): Example2.Person = new().apply(fn).build()

    fun apply(fn: Example2.Person.Builder.() -> Unit): Example2.Person = invoke(fn)

    fun let(fn: (Example2.Person.Builder) -> Unit): Example2.Person = new().apply(fn).build()
}