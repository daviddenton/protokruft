package org.protokruft.codeBuilders

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.KModifier.PRIVATE
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import io.grpc.Channel
import org.protokruft.GrpcService


fun buildGrpcClient(service: GrpcService, interfaceName: ClassName, serviceName: String): TypeSpec =
        TypeSpec.classBuilder("Client").apply {
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
        }.build()
