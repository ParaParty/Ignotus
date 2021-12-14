package com.tairitsu.ignotus.exception

import org.springframework.http.HttpStatus

/**
 * 本项目中的所有的接口运行时错误均从这里继承
 */
abstract class ApiException : RuntimeException {
    constructor() : super()

    /**
     * @param message 错误信息
     */
    constructor(message: String) : super(message)

    /**
     * @param message 错误信息
     * @param cause 异常
     */
    constructor(message: String, cause: Throwable?) : super(message, cause)

    /**
     * @param cause 异常
     */
    constructor(cause: Throwable?) : super(cause)

    /**
     * 序列化为错误信息数组
     */
    abstract fun toJSONArray(): Iterable<Any>

    /**
     * 获取 HTTP 状态码
     * 当发生错误时，这个返回值会被用于 HTTP 状态码
     */
    abstract fun getHttpStatus(): HttpStatus
}
