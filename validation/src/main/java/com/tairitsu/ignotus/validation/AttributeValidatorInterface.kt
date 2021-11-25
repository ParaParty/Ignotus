package com.tairitsu.ignotus.validation

/**
 * 验证器接口
 */
interface AttributeValidatorInterface : (String, Any?, Any?, (String) -> Unit) -> Unit {
    /**
     * 验证器
     *
     * @param [attribute]: 属性名
     * @param [arg]: 验证器传入的参数
     * @param [value]: 被验证的值
     * @param [fail]: 验证失败的回调函数，如果验证成功就别调这个函数了
     */
    override fun invoke(attribute: String, arg: Any?, value: Any?, fail: (String) -> Unit)
}
