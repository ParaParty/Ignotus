package moe.bit.ignotusdemo.config

import moe.bit.ignotusdemo.model.table.GroupTable
import moe.bit.ignotusdemo.model.table.UserTable
import org.jetbrains.exposed.spring.SpringTransactionManager
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class AutoMigration(@Suppress("UNUSED_PARAMETER") springTransactionManager: SpringTransactionManager) {
    @PostConstruct
    fun init() {
        transaction {
            SchemaUtils.create(UserTable)
            SchemaUtils.create(GroupTable)
        }
    }
}
