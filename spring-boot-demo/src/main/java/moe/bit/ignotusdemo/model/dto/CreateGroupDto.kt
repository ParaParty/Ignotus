package moe.bit.ignotusdemo.model.dto

import com.tairitsu.ignotus.validation.annotation.Min
import com.tairitsu.ignotus.validation.annotation.Required

class CreateGroupDto {
    @Required
    @Min(4)
    lateinit var name: String
}
