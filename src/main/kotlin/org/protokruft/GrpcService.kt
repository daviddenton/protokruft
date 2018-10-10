package org.protokruft

import com.squareup.kotlinpoet.ClassName

data class GrpcService(val className: ClassName, val methods: List<GrpcMethod>)