package ru.krupt.demo.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import ru.krupt.demo.domain.CallEntity
import ru.krupt.demo.domain.CallEntityInvocationInfo

interface CallRepository : JpaRepository<CallEntity, Long> {

    @Transactional(readOnly = true)
    fun findOneByNameIgnoreCase(name: String): CallEntity?

    @Transactional(readOnly = true)
    fun findInvocationInfoByNameIgnoreCase(name: String): CallEntityInvocationInfo?
}
