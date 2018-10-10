package org.protokruft

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FileSpec.Builder
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier.ABSTRACT
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.KModifier.PRIVATE
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import io.grpc.Channel

typealias TargetServiceClasses = () -> Iterable<GrpcService>

object GenerateProtobufServiceDsl {
    fun generate(
            services: TargetServiceClasses,
            outputFilename: String,
            serviceDslSuffix: String = "",
            nameFn: (ClassName) -> String = { it.toSimpleNames().replace("Grpc", serviceDslSuffix) }
    ): List<FileSpec> {
        fun Builder.generateFunctionFor(service: GrpcService): Builder {
            val serviceName = nameFn(service.className).removeSuffix(serviceDslSuffix)
            return apply {
                val interfaceName = ClassName.bestGuess(nameFn(service.className))
                addType(TypeSpec.interfaceBuilder(interfaceName).apply {
                    service.methods.forEach {
                        addFunction(
                                FunSpec.builder(it.name).apply {
                                    addModifiers(ABSTRACT)
                                    it.parameters.forEach {
                                        addParameter(it.simpleName.decapitalize(), it)
                                    }
                                    returns(it.returnType)
                                }.build()
                        )
                    }

                    addType(TypeSpec.objectBuilder("Grpc")
                            .addType(
                                    TypeSpec.classBuilder("Client")
                                            .apply {
                                                addFunction(FunSpec.constructorBuilder().apply {
                                                    addParameter("channel", Channel::class.asTypeName())
                                                            .addStatement("stub = ${service.className.simpleName}.newBlockingStub(channel)")
                                                }.build())
                                                addSuperinterface(interfaceName)
                                                addProperty(PropertySpec.builder("stub", service.className.nestedClass(serviceName + "BlockingStub")).addModifiers(PRIVATE).build())
                                                service.methods.forEach {
                                                    addFunction(
                                                            FunSpec.builder(it.name).apply {
                                                                addModifiers(OVERRIDE)
                                                                it.parameters.forEach {
                                                                    addParameter(it.simpleName.decapitalize(), it)
                                                                }
                                                                returns(it.returnType)
                                                            }
                                                                    .addStatement("return stub.${it.name}(${it.parameters.joinToString(",")
                                                                    { it.simpleName.decapitalize() }})")
                                                                    .build()
                                                    )
                                                }
                                            }
                                            .build()
                            )
                            .addType(
                                    TypeSpec.classBuilder("Server")
                                            .apply {
                                                superclass(service.className.nestedClass(serviceName + "ImplBase"))
                                                primaryConstructor(
                                                        FunSpec.constructorBuilder().addParameter("delegate", interfaceName).build()
                                                )
                                                addProperty(
                                                        PropertySpec.builder("delegate", interfaceName).addModifiers(PRIVATE).initializer("delegate").build()
                                                )
                                                service.methods.forEach {
                                                    addFunction(
                                                            FunSpec.builder(it.name).apply {
                                                                addModifiers(OVERRIDE)
                                                                it.parameters.forEach {
                                                                    addParameter(it.simpleName.decapitalize(), it)
                                                                }
                                                                addParameter(
                                                                        ParameterSpec.builder(
                                                                                "responseObserver",
                                                                                ClassName("io.grpc.stub", "StreamObserver").parameterizedBy(it.returnType)
                                                                        ).build()
                                                                )
                                                                val params = it.parameters.map { it.simpleName.decapitalize() }.joinToString(", ")
                                                                addStatement("responseObserver.onNext(delegate.${it.name}($params))")
                                                                addStatement("responseObserver.onCompleted()")
                                                            }.build()
                                                    )
                                                }
                                            }.build()
                            )
                            .build())
                }.build())
            }
        }

        return services()
                .groupBy { it.className.packageName }
                .map { (pkg, classes) ->
                    FileSpec.builder(pkg, outputFilename).apply {
                        classes.sortedBy { it.className.simpleName }.forEach { generateFunctionFor(it) }
                    }.build()
                }
    }
}