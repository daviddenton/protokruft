package org.protokruft

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FileSpec.Builder
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier.ABSTRACT
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import java.lang.reflect.Modifier

typealias TargetServiceClasses = () -> Iterable<ClassName>

object GenerateProtobufServiceDsl {
    fun generate(
            classNames: TargetServiceClasses,
            outputFilename: String,
            nameFn: (ClassName) -> String = { it.toSimpleNames().replace("BlockingStub", "") }
    ): List<FileSpec> {
        fun Builder.generateFunctionFor(clz: ClassName) =
                apply {
                    val methods = Class.forName(clz.reflectionName()).declaredMethods.filter { Modifier.isPublic(it.modifiers) }
                    addType(TypeSpec.interfaceBuilder(nameFn(clz)).apply {
                        methods.forEach {
                            addFunction(
                                    FunSpec.builder(it.name).apply {
                                        addModifiers(ABSTRACT)
                                        it.parameterTypes.forEach {
                                            addParameter(it.simpleName, it.asTypeName())
                                        }
                                        returns(it.returnType.asTypeName())
                                    }.build()
                            )
                        }
                    }.build())
                }

        return classNames()
                .filter { it.simpleName().endsWith("BlockingStub") }
                .groupBy { it.packageName() }
                .map { (pkg, classes) ->
                    FileSpec.builder(pkg, outputFilename).apply {
                        classes.sortedBy { it.reflectionName() }.forEach { generateFunctionFor(it) }
                    }.build()
                }
    }
}