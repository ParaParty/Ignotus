package com.tairitsu.ignotus.validation

/**
 * 将这个注解放在验证器注解上。如果这个验证器需要一个参数，则需要提供 value 属性。
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidatorAnnotation
