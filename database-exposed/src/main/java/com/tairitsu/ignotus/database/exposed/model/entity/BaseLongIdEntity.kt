package com.tairitsu.ignotus.database.exposed.model.entity

import com.tairitsu.ignotus.database.exposed.model.table.BaseLongIdTable
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import java.time.LocalDateTime

/**
 * Reference: https://github.com/paulkagiri/ExposedDatesAutoFill
 */
abstract class BaseLongIdEntity(id: EntityID<Long>, table: BaseLongIdTable) : LongEntity(id) {
    val createdAt by table.createdAt
    var updatedAt by table.updatedAt
}

/**
 * Reference: https://github.com/paulkagiri/ExposedDatesAutoFill
 */
abstract class BaseLongIdEntityClass<E : BaseLongIdEntity>(table: BaseLongIdTable) : LongEntityClass<E>(table) {
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
