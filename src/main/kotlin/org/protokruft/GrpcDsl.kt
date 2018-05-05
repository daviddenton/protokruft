package org.protokruft

import com.google.protobuf.GeneratedMessageV3
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FileSpec.Builder
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import org.reflections.Reflections
import kotlin.reflect.KClass

class GrpcDsl(private val pkg: String, private val file: String) {
    private fun <T : GeneratedMessageV3> Builder.generateFunctionFor(clz: KClass<T>) =
            apply {
                addFunction(
                        FunSpec.builder(clz.java.simpleName.decapitalize()).apply {
                            addParameter("fn", LambdaTypeName.get(
                                    receiver = clz.nestedClasses.find { it.simpleName == "Builder" }!!.asClassName(),
                                    returnType = Unit::class.asTypeName()))
                            addStatement("return ${clz.qualifiedName}.newBuilder().apply(fn).build()")
                            returns(clz)
                        }.build()
                )
            }

    private fun forClass(clz: List<KClass<out GeneratedMessageV3>>) =
            FileSpec.builder(pkg, file).apply {
                clz.forEach { generateFunctionFor(it) }
            }.build()

    fun generateAll() =
            forClass(Reflections(pkg).getSubTypesOf(GeneratedMessageV3::class.java)
                    .map { it.kotlin }
                    .sortedBy { it.qualifiedName })
}