package org.protokruft

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.FileSpec.Builder
import com.squareup.kotlinpoet.KModifier.OPERATOR
import com.squareup.kotlinpoet.KModifier.PRIVATE

typealias TargetMessageClasses = () -> Iterable<ClassName>

object GenerateProtobufMessageDsl {
    fun generate(
            classNames: TargetMessageClasses,
            outputFilename: String,
            messageDslPrefix: String = "new",
            nameFn: (ClassName) -> String = { "$messageDslPrefix${it.toSimpleNames()}" }
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
                .groupBy { it.packageName }
                .map { (pkg, classes) ->
                    FileSpec.builder(pkg, outputFilename).apply {
                        classes.sortedBy { it.reflectionName() }.forEach { generateFunctionFor(it) }
                    }.build()
                }
    }
}