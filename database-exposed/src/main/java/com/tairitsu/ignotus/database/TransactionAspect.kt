package com.tairitsu.ignotus.database

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Component

@Aspect
@Component
open class TransactionAspect {
    @Around("@annotation(com.tairitsu.ignotus.database.annotation.Transaction)")
    fun transactionHandler(joinPoint: ProceedingJoinPoint): Any? {
        var result: Any? = null
        transaction {
            result = joinPoint.proceed()
        }
        return result
    }
}
