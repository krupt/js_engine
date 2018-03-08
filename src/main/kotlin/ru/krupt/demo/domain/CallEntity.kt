package ru.krupt.demo.domain

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.Parameter
import org.hibernate.annotations.TypeDef
import ru.krupt.demo.util.jpa.hibernate.PgJsonbType
import ru.krupt.demo.variable.InvocationInfo
import javax.persistence.*

@Entity
@Table(name = "calls")
@TypeDef(name = "InvocationInfoPgJsonbType",
        typeClass = PgJsonbType::class,
        defaultForType = InvocationInfo::class,
        parameters = [Parameter(name = PgJsonbType.Companion.TYPE_CLASS_PROPERTY_NAME,
                value = "ru.krupt.demo.variable.InvocationInfo")])
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
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
        val version: Int = 0)

/**
 * Extract only invocationInfo to avoid transfer many bytes from database
 */
interface CallEntityInvocationInfo {
    fun getInvocationInfo(): InvocationInfo
}
