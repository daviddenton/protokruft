package org.protokruft.codeBuilders

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier.ABSTRACT
import com.squareup.kotlinpoet.TypeSpec
import org.protokruft.GrpcService

fun TypeSpec.Builder.buildInterface(service: GrpcService) {
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
}