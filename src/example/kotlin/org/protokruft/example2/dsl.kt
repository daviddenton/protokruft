package org.protokruft.example2

fun address(fn: Example2.Address.Builder.() -> Unit): Example2.Address = org.protokruft.example2.Example2.Address.newBuilder().apply(fn).build()

fun person(fn: Example2.Person.Builder.() -> Unit): Example2.Person = org.protokruft.example2.Example2.Person.newBuilder().apply(fn).build()
