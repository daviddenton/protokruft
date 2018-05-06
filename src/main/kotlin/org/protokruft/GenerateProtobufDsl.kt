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

interface TargetMessageClasses : () -> Iterable<Class<out GeneratedMessageV3>> {
    companion object {
        fun ScanClasspath(pkg: String): TargetMessageClasses = object : TargetMessageClasses {
            override fun invoke() = Reflections(pkg).getSubTypesOf(GeneratedMessageV3::class.java)
        }
    }
}

object GenerateProtobufDsl {
    fun generate(
            classes: TargetMessageClasses,
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

        return classes()
                .groupBy { it.`package` }
                .mapValues { it.value.map { it.kotlin }.sortedBy { it.qualifiedName } }
                .toList()
                .sortedBy { it.first.name }
                .map { pair -> pair.first.name to pair.second.map { it.asClassName()} }
                .map { (pkg, classes) ->
                    FileSpec.builder(pkg, outputFilename).apply {
                        classes.forEach { generateFunctionFor(it) }
                    }.build()
                }
    }
}