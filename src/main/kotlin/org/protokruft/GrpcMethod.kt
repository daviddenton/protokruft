package org.protokruft

import com.squareup.kotlinpoet.ClassName

data class GrpcMethod(val name: String, val parameters: List<ClassName>, val returnType: ClassName)