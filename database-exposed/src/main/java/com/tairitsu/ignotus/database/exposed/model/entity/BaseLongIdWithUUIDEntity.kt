package com.tairitsu.ignotus.database.exposed.model.entity

import com.tairitsu.ignotus.database.exposed.model.table.BaseLongIdWithUUIDTable
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import java.time.LocalDateTime

abstract class BaseLongIdWithUUIDEntity(id: EntityID<Long>, table: BaseLongIdWithUUIDTable) : LongEntity(id) {
    val uuid by table.uuid
    val createdAt by table.createdAt
    var updatedAt by table.updatedAt
}

abstract class BaseLongIdWithUUIDEntityClass<E : BaseLongIdWithUUIDEntity>(table: BaseLongIdWithUUIDTable) :
    LongEntityClass<E>(table) {
    init {
        EntityHook.subscribe { action ->
            if (action.changeType == EntityChangeType.Updated || action.changeType == EntityChangeType.Created) {
                try {
                    action.toEntity(this)?.updatedAt = LocalDateTime.now()
                } catch (ignored: Exception) {
                    //nothing much to do here
                }
            }
        }
    }
}
