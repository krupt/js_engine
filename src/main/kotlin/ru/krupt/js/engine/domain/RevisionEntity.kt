package ru.krupt.js.engine.domain

import org.hibernate.envers.RevisionEntity
import org.hibernate.envers.RevisionNumber
import org.hibernate.envers.RevisionTimestamp
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "rev_info")
@RevisionEntity
class RevisionEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RevisionEntityIdGenerator")
        @SequenceGenerator(name = "RevisionEntityIdGenerator", sequenceName = "rev_info_id_seq")
        @RevisionNumber
        val id: Long? = null,

        @RevisionTimestamp
        val timestamp: Date = Date()
)
