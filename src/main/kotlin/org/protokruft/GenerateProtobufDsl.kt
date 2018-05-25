package org.protokruft

import com.google.protobuf.GeneratedMessageV3
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FileSpec.Builder
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier.OPERATOR
import com.squareup.kotlinpoet.KModifier.PRIVATE
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.TypeSpec
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

private fun ClassName.toSimpleNames() = reflectionName().run {
    if (contains("$")) substringAfter("$").replace("$", "") else simpleName()
}.replace(".", "")

object GenerateProtobufDsl {
    fun generate(
            classNames: TargetMessageClasses,
            outputFilename: String,
            nameFn: (ClassName) -> String = { "new${it.toSimpleNames()}" }
    ): List<FileSpec> {
        fun Builder.generateFunctionFor(clz: ClassName) =
                apply {
                    addType(TypeSpec.objectBuilder(nameFn(clz)).apply {
                        addFunction(
                                FunSpec.builder("new").apply {
                                    addModifiers(PRIVATE)
                                    addStatement("return ${clz.canonicalName}.newBuilder()")
                                    returns(clz.nestedClass("Builder"))
                                }.build()
                        )
                        addFunction(
                                FunSpec.builder("invoke").apply {
                                    addModifiers(OPERATOR)
                                    addParameter("fn", LambdaTypeName.get(
                                            receiver = clz.nestedClass("Builder"),
                                            returnType = Unit::class.asTypeName()))
                                    addStatement("return new().apply(fn).build()")
                                    returns(clz)
                                }.build()
                        )
                        addFunction(
                                FunSpec.builder("apply").apply {
                                    addParameter("fn", LambdaTypeName.get(
                                            receiver = clz.nestedClass("Builder"),
                                            returnType = Unit::class.asTypeName()))
                                    addStatement("return invoke(fn)")
                                    returns(clz)
                                }.build()
                        )
                        addFunction(
                                FunSpec.builder("also").apply {
                                    addParameter("fn", LambdaTypeName.get(
                                            receiver = null,
                                            parameters = *arrayOf(clz.nestedClass("Builder")),
                                            returnType = Unit::class.asTypeName()

                                    ))
                                    addStatement("return new().apply(fn).build()")
                                    returns(clz)
                                }.build()
                        )
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