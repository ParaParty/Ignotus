package moe.bit.ignotusdemo.exception

import com.tairitsu.ignotus.exception.SingleApiException

class UsernameOrPasswordIsWrong : SingleApiException(403, "username_or_password_wrong", "The username or password is wrong.")
