package moe.bit.ignotusdemo.model.table

import com.tairitsu.ignotus.database.exposed.model.table.BaseLongIdWithUUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object UserTable : BaseLongIdWithUUIDTable("users") {
    val username: Column<String> = varchar("username", 256)
    val password: Column<String> = varchar("password", 256)
    val lastLoginAt: Column<LocalDateTime?> = datetime("last_login_at").nullable()
}
