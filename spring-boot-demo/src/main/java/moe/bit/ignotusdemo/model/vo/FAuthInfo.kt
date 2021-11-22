package moe.bit.ignotusdemo.model.vo

import java.time.LocalDateTime
import java.util.function.Consumer

class FAuthInfo {
    var id: String = ""
    var username: String = ""

    var createdAt: LocalDateTime = LocalDateTime.now()
    var updatedAt: LocalDateTime = LocalDateTime.now()

    var roles: ArrayList<String> = ArrayList()

    /**
     * 判断该用户是否拥有某些权限
     */
    fun checkRoles(
        requiresRoles: Array<String>,
        fail: Consumer<String> = Consumer { },
    ): Boolean {
        var result = true
        for (role in requiresRoles) {
            if (this.roles.contains(role)) {
                continue
            }
            fail.accept(role)
            result = false
        }
        return result
    }

    /**
     * 判断该用户是否拥有某个权限
     */
    fun checkRole(
        requiresRole: String,
        fail: Consumer<String> = Consumer { },
    ): Boolean {
        if (!this.roles.contains(requiresRole)) {
            fail.accept(requiresRole)
            return false
        }
        return true
    }

    companion object {
        @JvmField
        val GUEST = FAuthInfo().apply {
            id = "-1"
            username = "guest"
            createdAt = LocalDateTime.now()
            updatedAt = createdAt
            roles = ArrayList<String>().also {
                it.add("GUEST")
            }
        }
    }
}
