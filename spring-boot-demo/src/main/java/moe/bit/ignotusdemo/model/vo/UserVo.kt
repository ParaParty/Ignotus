package moe.bit.ignotusdemo.model.vo

import moe.bit.ignotusdemo.model.entity.UserEntity
import com.tairitsu.ignotus.serializer.vo.BaseResponse
import java.time.LocalDateTime

class UserVo : BaseResponse {
    override val modelType = "users"

    override var id: String = ""

    var username: String? = null
    var createdAt: LocalDateTime? = null
    var updatedAt: LocalDateTime? = null

    constructor(userEntity: UserEntity) {
        this.id = userEntity.uuid
        this.username = userEntity.username
        this.createdAt = userEntity.createdAt
        this.updatedAt = userEntity.updatedAt
    }
}
