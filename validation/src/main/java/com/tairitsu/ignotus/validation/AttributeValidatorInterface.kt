package com.tairitsu.ignotus.validation

interface AttributeValidatorInterface : (String, Any?, Any?, (String) -> Unit) -> Unit {
    /**
     * [attribute]: 属性名
     * [arg]: 验证器传入的参数
     * [value]: 被验证的值
     * [fail]: 验证失败的回调函数，如果验证成功就别调这个函数了
     */
    override fun invoke(attribute: String, arg: Any?, value: Any?, fail: (String) -> Unit)
}
