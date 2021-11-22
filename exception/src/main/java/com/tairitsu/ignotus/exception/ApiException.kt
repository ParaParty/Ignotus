package com.tairitsu.ignotus.exception

/**
 * 本项目中的所有的接口运行时错误均从这里继承
 */
abstract class ApiException : RuntimeException {
    constructor() : super()

    constructor(message: String) : super(message)

    abstract fun toJSONArray(): ArrayList<Any>
}

