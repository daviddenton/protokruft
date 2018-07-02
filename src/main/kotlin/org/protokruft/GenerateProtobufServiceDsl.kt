package org.protokruft

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FileSpec.Builder
import com.squareup.kotlinpoet.TypeSpec

typealias TargetServiceClasses = () -> Iterable<ClassName>

object GenerateProtobufServiceDsl {
    fun generate(
            classNames: TargetServiceClasses,
            outputFilename: String,
            nameFn: (ClassName) -> String = { "new${it.toSimpleNames()}" }
    ): List<FileSpec> {
        fun Builder.generateFunctionFor(clz: ClassName) =
                apply {
                    addType(TypeSpec.objectBuilder(nameFn(clz)).apply {
                        // generate code here!
                    }.build())
                }

        return classNames()
                .groupBy { it.packageName() }
                .map { (pkg, classes) ->
                    FileSpec.builder(pkg, outputFilename).apply {
                        classes.sortedBy { it.reflectionName() }.forEach { generateFunctionFor(it) }
                    }.build()
                }
    }
}