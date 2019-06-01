package org.protokruft.codeBuilders

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier.OPERATOR
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import io.grpc.Channel
import org.protokruft.GrpcService
import java.time.Duration
import java.util.concurrent.TimeUnit

fun buildGrpcClient(service: GrpcService, interfaceName: ClassName) =
        TypeSpec.objectBuilder("Client").apply {
            addFunction(FunSpec.builder("invoke").apply {
                addModifiers(OPERATOR)
                addParameter("channel", Channel::class.asTypeName())
                addParameter(ParameterSpec.builder("timeout", Duration::class.asTypeName())
                    .defaultValue("Duration.ofSeconds(10)")
                    .build())
                returns(interfaceName)
                addStatement("val stub = ${service.className.simpleName}.newBlockingStub(channel)")
                addStatement("return ${TypeSpec.anonymousClassBuilder().apply {
                    addSuperinterface(interfaceName)
                    service.methods.forEach {
                        addFunction(
                                FunSpec.builder(it.name).apply {
                                    addModifiers(OVERRIDE)
                                    it.parameters.forEach {
                                        addParameter(it.simpleName.decapitalize(), it)
                                    }
                                    returns(it.returnType)
                                }
                                        .addStatement("return stub.withDeadlineAfter(timeout.toMillis(), %T.MILLISECONDS).${it.name}(${it.parameters.joinToString(",")
                                        { it.simpleName.decapitalize() }})", TimeUnit::class.java)
                                        .build()
                        )
                    }
                }.build()}")
            }.build())
        }.build()