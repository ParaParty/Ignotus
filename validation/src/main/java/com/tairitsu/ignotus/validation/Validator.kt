package com.tairitsu.ignotus.validation

import kotlin.reflect.KClass


interface Validator {

    /**
     * 验证器入口
     *
     * @param content 被验证的对象
     * @param basePath 输出报错的时候，报错信息的基础路径
     */
    fun validate(content: Any?, basePath: String = "")

    /**
     * 验证器入口
     */
    fun <T : Any> validate(content: String?, validation: Map<String, Any>, basePath: String = "", type: KClass<T>): T

    /**
     * 验证器入口
     */
    fun <T> validate(content: String?, validation: Map<String, Any>, basePath: String = "", type: Class<T>): T

    /**
     * 验证器入口
     */
    fun validate(content: String?, validation: Map<String, Any>, basePath: String = "")

    /**
     * 验证器入口
     *
     * [content] 被验证的内容，非空
     * [validation] 验证器 键为 json 路径，值为字符串或字符串数组或验证器实例。
     * 字符串格式为 `"验证器1|验证器2:验证器2的参数"`
     * 字符串数组格式为 `["验证器1"， "验证器2:验证器2的参数"]`
     * 验证器实例
     *
     * [basePath] 报错的根
     * 如传递 [basePath] = "test" ，[validation] 的某个键为 foo，若验证失败报错的路径为 "test.foo"
     *
     * @throw ApiExceptionBag
     */
    fun validate(content: Map<String, Any?>, validation: Map<String, Any>, basePath: String = "")
}
