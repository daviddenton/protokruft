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

interface GeneratedClassesProvider : () -> Iterable<Class<out GeneratedMessageV3>> {
    companion object {
        fun ScanClasspath(pkg: String): GeneratedClassesProvider = object : GeneratedClassesProvider {
            override fun invoke() = Reflections(pkg).getSubTypesOf(GeneratedMessageV3::class.java)
        }
    }
}

object GrpcDsl {
    fun decruftinate(
            classesProvider: GeneratedClassesProvider,
            file: String,
            nameFn: (KClass<out GeneratedMessageV3>) -> String = { it.java.simpleName.decapitalize() }
    ): List<FileSpec> {
        fun <T : GeneratedMessageV3> Builder.generateFunctionFor(clz: KClass<T>) =
                apply {
                    addFunction(
                            FunSpec.builder(nameFn(clz)).apply {
                                addParameter("fn", LambdaTypeName.get(
                                        receiver = clz.nestedClasses.find { it.simpleName == "Builder" }!!.asClassName(),
                                        returnType = Unit::class.asTypeName()))
                                addStatement("return ${clz.qualifiedName}.newBuilder().apply(fn).build()")
                                returns(clz)
                            }.build()
                    )
                }

        return classesProvider()
                .groupBy { it.`package` }
                .mapValues { it.value.map { it.kotlin }.sortedBy { it.qualifiedName } }
                .toList()
                .sortedBy { it.first.name }
                .map { (pkg, classes) ->
                    FileSpec.builder(pkg.name, file).apply {
                        classes.forEach { generateFunctionFor(it) }
                    }.build()
                }
    }
}