package ru.krupt.js.engine.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import ru.krupt.js.engine.domain.CallEntity
import ru.krupt.js.engine.domain.CallEntityWithoutBody

interface CallRepository : JpaRepository<CallEntity, Long> {

    fun findOneByNameIgnoreCase(name: String): CallEntity?

    fun findOneWithoutBodyByNameIgnoreCase(name: String): CallEntityWithoutBody?

    @Transactional
    fun deleteByNameIgnoreCase(name: String): Int

    /**
     * We can't use findAll cause we need custom model
     * The method ends with 'By' cause we need hack Spring's PartTree
     *
     * @see org.springframework.data.repository.query.parser.PartTree
     */
    fun findAllWithoutBodyBy(): List<CallEntityWithoutBody>
}
