package com.tairitsu.ignotus.database.model.table

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

/**
 * Reference: https://github.com/paulkagiri/ExposedDatesAutoFill
 */
abstract class BaseLongIdTable(name: String) : LongIdTable(name) {
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").nullable()
}
