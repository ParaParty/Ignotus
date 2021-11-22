package com.tairitsu.ignotus.validation

/**
 * 在 Controller 的参数里插入，表示要验证这个参数
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Valid
