package org.protokruft

import com.google.protobuf.GeneratedMessageV3
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FileSpec.Builder
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import org.reflections.Reflections

typealias TargetMessageClasses = () -> Iterable<ClassName>

fun ScanClasspath(pkg: String) = {
    Reflections(pkg).getSubTypesOf(GeneratedMessageV3::class.java)
            .map { it.kotlin }
            .sortedBy { it.qualifiedName }
            .map { it.asClassName() }
}

object GenerateProtobufDsl {
    fun generate(
            classNames: TargetMessageClasses,
            outputFilename: String,
            nameFn: (ClassName) -> String = { it.simpleName().decapitalize() }
    ): List<FileSpec> {
        fun Builder.generateFunctionFor(clz: ClassName) =
                apply {
                    addFunction(
                            FunSpec.builder(nameFn(clz)).apply {
                                addParameter("fn", LambdaTypeName.get(
                                        receiver = clz.nestedClass("Builder"),
                                        returnType = Unit::class.asTypeName()))
                                addStatement("return ${clz.canonicalName}.newBuilder().apply(fn).build()")
                                returns(clz)
                            }.build()
                    )
                }

        return classNames()
                .groupBy { it.packageName() }
                .map { (pkg, classes) ->
                    FileSpec.builder(pkg, outputFilename).apply {
                        classes.forEach { generateFunctionFor(it) }
                    }.build()
                }
    }
}