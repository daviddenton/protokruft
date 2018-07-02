package org.protokruft

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FileSpec.Builder
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier.ABSTRACT
import com.squareup.kotlinpoet.KModifier.PRIVATE
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import io.grpc.Channel
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
                    val interfaceName = nameFn(clz)
                    addType(TypeSpec.interfaceBuilder(interfaceName).apply {
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

                        addType(
                                TypeSpec.classBuilder("Grpc")
                                        .apply {
                                            addFunction(FunSpec.constructorBuilder().apply {
                                                addParameter("channel", Channel::class.asTypeName())
                                                        .addStatement("stub = ${interfaceName}BlockingStub(channel)")
                                            }.build())
                                            addSuperinterface(ClassName.bestGuess(interfaceName))
                                            addProperty(PropertySpec.builder("stub", Channel::class.asTypeName()).addModifiers(PRIVATE).build())
                                            methods.forEach {
                                                addFunction(
                                                        FunSpec.builder(it.name).apply {
                                                            it.parameterTypes.forEach {
                                                                addParameter(it.simpleName, it.asTypeName())
                                                            }
                                                            returns(it.returnType.asTypeName())
                                                        }
                                                                .addStatement("return stub.${it.name}(${it.parameterTypes.map { it.simpleName }.joinToString(",")}})")
                                                                .build()
                                                )
                                            }
                                        }
                                        .build()
                        )
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