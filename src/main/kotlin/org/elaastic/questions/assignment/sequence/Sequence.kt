package org.elaastic.questions.assignment.sequence

import org.elaastic.questions.assignment.Assignment
import org.elaastic.questions.assignment.ExecutionContext
import org.elaastic.questions.assignment.Statement
import org.elaastic.questions.directory.User
import org.elaastic.questions.persistence.AbstractJpaPersistable
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull


@Entity
@NamedEntityGraph(
        name = "Sequence.statement",
        attributeNodes = [
            NamedAttributeNode("statement"),
            NamedAttributeNode("assignment")
        ]
)
@EntityListeners(AuditingEntityListener::class)
class Sequence(
        @field:ManyToOne(fetch = FetchType.LAZY)
        var owner: User,

        @field:ManyToOne(fetch = FetchType.EAGER)
        var statement: Statement,

        @field:NotNull
        @field:ManyToOne(fetch = FetchType.EAGER)
        var assignment: Assignment? = null,

        var rank: Int = 0,

        @field:Enumerated(EnumType.STRING)
        var executionContext: ExecutionContext = ExecutionContext.FaceToFace

) : AbstractJpaPersistable<Long>(), Comparable<Sequence> {

    @Version
    var version: Long? = null

    @NotNull
    @Column(name = "date_created")
    @CreatedDate
    lateinit var dateCreated: Date

    @NotNull
    @LastModifiedDate
    @Column(name = "last_updated")
    var lastUpdated: Date? = null

    // TODO Interaction

    @Enumerated(EnumType.STRING)
    var state: State = State.beforeStart

    var resultsArePublished: Boolean = false

    override fun compareTo(other: Sequence): Int {
        return rank.compareTo(other.rank)
    }
}

enum class State {
    beforeStart,
    show,
    afterStop
}

