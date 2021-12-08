package com.tairitsu.ignotus.database.exposed.model.table

import com.tairitsu.ignotus.support.util.UUIDUtils
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

abstract class BaseLongIdWithUUIDTable(name: String) : LongIdTable(name) {
    val uuid = varchar("uuid", 36).clientDefault { UUIDUtils.createNoLeadingDigitId() }.uniqueIndex()
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").nullable()
}
