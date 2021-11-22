package moe.bit.ignotusdemo.model.vo

import com.tairitsu.ignotus.serializer.vo.BaseResponse
import com.tairitsu.ignotus.support.util.UUIDUtils.createModelId

open class Credentials : BaseResponse() {
    override val modelType = "credentials"

    override var id = createModelId()

    open var tokenType = "Bearer";

    open var expiresIn = 0;

    open var accessToken = "";

    open var status = 0;
}
