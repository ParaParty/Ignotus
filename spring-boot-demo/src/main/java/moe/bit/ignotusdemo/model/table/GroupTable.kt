package moe.bit.ignotusdemo.model.table

import com.tairitsu.ignotus.database.exposed.model.table.BaseLongIdTable

object GroupTable : BaseLongIdTable("groups") {
    val name = varchar("name", 256)
}
