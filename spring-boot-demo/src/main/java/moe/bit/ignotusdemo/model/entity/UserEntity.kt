package moe.bit.ignotusdemo.model.entity

import com.tairitsu.ignotus.database.exposed.model.entity.BaseLongIdWithUUIDEntity
import com.tairitsu.ignotus.database.exposed.model.entity.BaseLongIdWithUUIDEntityClass
import moe.bit.ignotusdemo.model.table.UserTable
import org.jetbrains.exposed.dao.id.EntityID
import java.util.function.Consumer

class UserEntity(id: EntityID<Long>) : BaseLongIdWithUUIDEntity(id, UserTable) {
    companion object : BaseLongIdWithUUIDEntityClass<UserEntity>(UserTable) {
        @JvmStatic
        fun create(init: Consumer<UserEntity>) = new { init.accept(this) }
    }

    var username by UserTable.username
    var password by UserTable.password
    var lastLoginAt by UserTable.lastLoginAt
}
