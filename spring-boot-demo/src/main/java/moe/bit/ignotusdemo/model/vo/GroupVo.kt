package moe.bit.ignotusdemo.model.vo

import moe.bit.ignotusdemo.model.entity.GroupEntity
import com.tairitsu.ignotus.serializer.vo.BaseResponse

class GroupVo : BaseResponse {
    override val modelType = "groups"

    override var id: String = ""

    var name: String? = null

    constructor(model: GroupEntity) {
        this.id = model.id.toString()
        this.name = model.name
    }
}
