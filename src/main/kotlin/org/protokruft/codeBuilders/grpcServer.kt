package org.protokruft.codeBuilders

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.KModifier.PRIVATE
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import org.protokruft.GrpcService

fun buildGrpcServer(service: GrpcService, serviceName: String, interfaceName: ClassName): TypeSpec =
        TypeSpec.classBuilder("Server").apply {
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
                            val params = it.parameters.joinToString(", ") { it.simpleName.decapitalize() }
                            addStatement("responseObserver.onNext(delegate.${it.name}($params))")
                            addStatement("responseObserver.onCompleted()")
                        }.build()
                )
            }
        }.build()
