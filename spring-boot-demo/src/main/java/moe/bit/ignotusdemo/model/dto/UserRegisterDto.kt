package moe.bit.ignotusdemo.model.dto

import com.tairitsu.ignotus.validation.annotation.Min
import com.tairitsu.ignotus.validation.annotation.Required

class UserRegisterDto {
    @Required
    @Min(6)
    lateinit var username: String

    @Required
    @Min(6)
    lateinit var password: String
}
