package moe.bit.ignotusdemo.exception

import com.tairitsu.ignotus.exception.SingleApiException

class UserNotFoundException : SingleApiException(404, "user_not_exists", "The user not exists")
