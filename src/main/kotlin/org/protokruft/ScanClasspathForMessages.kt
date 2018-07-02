package org.protokruft

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import org.reflections.Reflections

fun ScanClasspathFor(pkg: String, clazz: Class<*>): () -> List<ClassName> = {
    Reflections(pkg).getSubTypesOf(clazz)
            .map { it.kotlin }
            .sortedBy { it.qualifiedName }
            .map { it.asClassName() }
}

fun ClassName.toSimpleNames() = reflectionName().run {
    if (contains("$")) substringAfter("$").replace("$", "") else simpleName()
}.replace(".", "")
