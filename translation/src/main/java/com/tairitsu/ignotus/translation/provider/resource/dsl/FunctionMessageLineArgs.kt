package com.tairitsu.ignotus.translation.provider.resource.dsl

import java.util.*

data class FunctionMessageLineArgs(
    val key: String,
    val args: Map<String, Any?>,
    val locale: Locale,
)
