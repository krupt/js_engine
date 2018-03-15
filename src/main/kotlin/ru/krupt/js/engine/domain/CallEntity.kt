package ru.krupt.js.engine.domain

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.Parameter
import org.hibernate.annotations.TypeDef
import org.hibernate.envers.Audited
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import ru.krupt.js.engine.util.jpa.hibernate.PgJsonbType
import ru.krupt.js.engine.variable.InvocationInfo
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "calls")
@TypeDef(name = "InvocationInfoPgJsonbType",
        typeClass = PgJsonbType::class,
        defaultForType = InvocationInfo::class,
        parameters = [Parameter(name = PgJsonbType.Companion.TYPE_CLASS_PROPERTY_NAME,
                value = "ru.krupt.js.engine.variable.InvocationInfo")])
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Audited
@EntityListeners(AuditingEntityListener::class)
data class CallEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CallEntityIdGenerator")
        @SequenceGenerator(name = "CallEntityIdGenerator", sequenceName = "calls_id_seq")
        val id: Long? = null,

        @Column(nullable = false, length = 100)
        val name: String,

        @Column(nullable = false)
        val description: String,

        @Column(nullable = false, columnDefinition = "jsonb")
        val invocationInfo: InvocationInfo,

        val body: String,

        @Version
        val version: Int = 0) {

    @LastModifiedDate
    lateinit var lastModifiedTime: LocalDateTime
}

/**
 * Extract only light-weight fields to avoid transfer many bytes from database
 */
interface CallEntityWithoutBody {
    val id: Long
    val name: String
    val description: String
    val invocationInfo: InvocationInfo
    val version: Int
}
