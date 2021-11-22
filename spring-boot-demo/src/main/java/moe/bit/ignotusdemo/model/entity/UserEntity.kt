package moe.bit.ignotusdemo.model.entity

import moe.bit.ignotusdemo.model.table.UserTable
import com.tairitsu.ignotus.database.model.entity.BaseLongIdEntity
import com.tairitsu.ignotus.database.model.entity.BaseLongIdEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.function.Consumer

class UserEntity(id: EntityID<Long>) : BaseLongIdEntity(id, UserTable) {
    companion object : BaseLongIdEntityClass<UserEntity>(UserTable) {
        @JvmStatic
        fun create(init: Consumer<UserEntity>) = new { init.accept(this) }
    }

    var username by UserTable.username
    var password by UserTable.password
    var lastLoginAt by UserTable.lastLoginAt
}

