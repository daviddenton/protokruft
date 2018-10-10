package org.protokruft

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FileSpec.Builder
import com.squareup.kotlinpoet.TypeSpec
import org.protokruft.codeBuilders.buildGrpcObject
import org.protokruft.codeBuilders.buildInterface

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
                addType(TypeSpec.interfaceBuilder(interfaceName)
                        .apply { buildInterface(service) }
                        .apply { addType(buildGrpcObject(service, interfaceName, serviceName)) }.build())
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
