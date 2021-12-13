package com.tairitsu.ignotus.exception

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
     * 序列化为错误信息数组
     */
    abstract fun toJSONArray(): Iterable<Any>
}

