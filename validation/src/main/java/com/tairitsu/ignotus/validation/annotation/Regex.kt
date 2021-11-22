package com.tairitsu.ignotus.validation.annotation

import com.tairitsu.ignotus.validation.ValidatorAnnotation

@ValidatorAnnotation
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Regex(val value: String)
