package com.tairitsu.ignotus.validation.annotation

import com.tairitsu.ignotus.validation.ValidatorAnnotation

/**
 * 被检查字段必须存在且不为空
 */
@ValidatorAnnotation
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Required
