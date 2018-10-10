package org.protokruft.codeBuilders

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeSpec
import org.protokruft.GrpcService

fun buildGrpcObject(service: GrpcService, interfaceName: ClassName, serviceName: String): TypeSpec =
        TypeSpec.objectBuilder("Grpc")
                .addType(buildGrpcClient(service, interfaceName, serviceName))
                .addType(buildGrpcServer(service, serviceName, interfaceName))
                .build()
