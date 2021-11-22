package com.tairitsu.ignotus.foundation.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class JsonApiController(
    val optionalInclude: Array<String> = [],
    val optionalSort: Array<String> = [],
)
