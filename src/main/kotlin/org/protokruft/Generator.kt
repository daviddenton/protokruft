package org.protokruft

import com.google.protobuf.GeneratedMessageV3
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FileSpec.Builder
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import kotlin.reflect.KClass

fun <T : GeneratedMessageV3> Builder.populateFor(clz: KClass<T>) =
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

class Generator(private val pkg: Package, private val file: String) {
    fun <T : GeneratedMessageV3> forClass(vararg clz: KClass<T>) =
            FileSpec.builder(pkg.name, file).apply {
                clz.forEach {
                    populateFor(it)
                }
            }.build()
}
