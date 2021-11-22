package com.tairitsu.ignotus.validation.annotation

import com.tairitsu.ignotus.validation.ValidatorAnnotation

/**
 * 被检查字段最大值为
 * 如果被检查字段为数字，则表示值最大为
 * 如果被检查字段为可迭代型，则表示最大长度为（如字符串最大长度，列表最大长度）
 */
@ValidatorAnnotation
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Max(val value: Long)

