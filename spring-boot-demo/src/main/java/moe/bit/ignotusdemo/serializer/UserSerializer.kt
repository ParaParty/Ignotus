//package moe.bit.ignotusdemo.serializer
//
//import com.tairitsu.ignotus.serializer.Serializer
//import moe.bit.ignotusdemo.model.vo.UserVo
//
//class UserSerializer: Serializer<UserVo>() {
//    override fun defaultAttributeSerialize(model: UserVo): Map<String, Any?> {
//        val ret =  mutableMapOf(
//                "username" to model.username,
//                "createdAt" to model.createdAt,
//                "updatedAt" to model.updatedAt,
//        )
//
////        if (request.getActorId() == model.id) {
////            ret["email"] = model.email;
////        }
//
//        return ret;
//    }
//}
//
