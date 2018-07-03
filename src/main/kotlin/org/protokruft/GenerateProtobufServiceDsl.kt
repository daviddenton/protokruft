package org.protokruft

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FileSpec.Builder
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier.ABSTRACT
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.KModifier.PRIVATE
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import io.grpc.Channel

//public static io.grpc.MethodDescriptor<org.protokruft.example1.Example1.Car,
//      org.protokruft.example1.Example1.Engine> getGetEngineMethod()

data class GrpcMethod(val name: String, val parameters: List<ClassName>, val returnType: ClassName)

data class GrpcService(val className: ClassName, val methods: List<GrpcMethod>)

typealias TargetServiceClasses = () -> Iterable<GrpcService>

object GenerateProtobufServiceDsl {
    fun generate(
            classNames: TargetServiceClasses,
            outputFilename: String,
            nameFn: (ClassName) -> String = { it.toSimpleNames().replace("Grpc", "") }
    ): List<FileSpec> {
        fun Builder.generateFunctionFor(service: GrpcService) =
                apply {
                    val interfaceName = nameFn(service.className)
                    addType(TypeSpec.interfaceBuilder(interfaceName).apply {
                        service.methods.forEach {
                            addFunction(
                                    FunSpec.builder(it.name).apply {
                                        addModifiers(ABSTRACT)
                                        it.parameters.forEach {
                                            addParameter(it.simpleName().decapitalize(), it)
                                        }
                                        returns(it.returnType)
                                    }.build()
                            )
                        }

                        addType(
                                TypeSpec.classBuilder("Grpc")
                                        .apply {
                                            addFunction(FunSpec.constructorBuilder().apply {
                                                addParameter("channel", Channel::class.asTypeName())
                                                        .addStatement("stub = ${service.className.simpleName()}.newBlockingStub(channel)")
                                            }.build())
                                            addSuperinterface(ClassName.bestGuess(interfaceName))
                                            addProperty(PropertySpec.builder("stub", service.className.nestedClass(nameFn(service.className)+"BlockingStub")).addModifiers(PRIVATE).build())
                                            service.methods.forEach {
                                                addFunction(
                                                        FunSpec.builder(it.name).apply {
                                                            addModifiers(OVERRIDE)
                                                            it.parameters.forEach {
                                                                addParameter(it.simpleName().decapitalize(), it)
                                                            }
                                                            returns(it.returnType)
                                                        }
                                                                .addStatement("return stub.${it.name}(${it.parameters.joinToString(",")
                                                                { it.simpleName().decapitalize() }})")
                                                                .build()
                                                )
                                            }
                                        }
                                        .build()
                        )
                    }.build())
                }

        return classNames()
                .groupBy { it.className.packageName() }
                .map { (pkg, classes) ->
                    FileSpec.builder(pkg, outputFilename).apply {
                        classes.sortedBy { it.className.simpleName() }.forEach { generateFunctionFor(it) }
                    }.build()
                }
    }
}