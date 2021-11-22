package moe.bit.ignotusdemo.model.entity

import moe.bit.ignotusdemo.model.table.GroupTable
import com.tairitsu.ignotus.database.model.entity.BaseLongIdEntity
import com.tairitsu.ignotus.database.model.entity.BaseLongIdEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.function.Consumer

class GroupEntity(id: EntityID<Long>) : BaseLongIdEntity(id, GroupTable) {
    companion object : BaseLongIdEntityClass<GroupEntity>(GroupTable) {
        @JvmStatic
        fun create(init: Consumer<GroupEntity>) = new { init.accept(this) }
    }

    var name by GroupTable.name
}


