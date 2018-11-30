package org.protokruft.codeBuilders

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier.OPERATOR
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import io.grpc.Channel
import org.protokruft.GrpcService

fun buildGrpcClient(service: GrpcService, interfaceName: ClassName, serviceName: String): TypeSpec {
    return TypeSpec.objectBuilder("Client").apply {
        addFunction(FunSpec.builder("invoke").apply {
            addModifiers(OPERATOR)
            addParameter("channel", Channel::class.asTypeName())
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
                                    .addStatement("return stub.${it.name}(${it.parameters.joinToString(",")
                                    { it.simpleName.decapitalize() }})")
                                    .build()
                    )
                }
            }.build()}")
            returns(interfaceName)
        }.build())
    }.build()
}
//    return TypeSpec.classBuilder("Client").apply {
//        addFunction(FunSpec.constructorBuilder().apply {
//            addParameter("channel", Channel::class.asTypeName())
//                    .addStatement("stub = ${service.className.simpleName}.newBlockingStub(channel)")
//        }.build())
//        addSuperinterface(interfaceName)
//        addProperty(PropertySpec.builder("stub", service.className.nestedClass(serviceName + "BlockingStub")).addModifiers(PRIVATE).build())
//    }
//}.build()
//}
