package com.tairitsu.ignotus.validation.validator

import com.tairitsu.ignotus.validation.AttributeValidatorInterface
import org.springframework.stereotype.Component

/**
 * 这是个特殊的保留验证器，没有任何实现。
 */
@Component
class ValidatorRequired : AttributeValidatorInterface {
    override fun invoke(attribute: String, arg: Any?, value: Any?, fail: (String) -> Unit) = Unit
}

