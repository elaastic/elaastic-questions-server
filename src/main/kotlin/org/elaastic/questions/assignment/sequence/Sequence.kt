package org.elaastic.questions.assignment.sequence

import org.elaastic.questions.assignment.Assignment
import org.elaastic.questions.assignment.ExecutionContext
import org.elaastic.questions.assignment.Statement
import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.assignment.sequence.interaction.InteractionType
import org.elaastic.questions.directory.User
import org.elaastic.questions.persistence.AbstractJpaPersistable
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.lang.IllegalStateException
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
        var executionContext: ExecutionContext = ExecutionContext.FaceToFace,

        @field:OneToOne
        var activeInteraction: Interaction? = null,

        @field:Enumerated(EnumType.STRING)
        var state: State = State.beforeStart,

        var resultsArePublished: Boolean = false


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

    @Transient
    var interactions: MutableMap<InteractionType, Interaction> = mutableMapOf()
        get() { // Needed because of JPA using a default empty constructor that bypass the var initialization...
            if (field == null) {
                field = mutableMapOf()
            }
            return field
        }

    @Transient
    fun getResponseSubmissionInteraction() =
            interactions[InteractionType.ResponseSubmission]
                    ?: throw IllegalStateException("The response submission interaction is not initialized")

    @Transient
    fun getReadInteraction() =
            interactions[InteractionType.Read]
                    ?: throw IllegalStateException("The read interaction is not initialized")


    override fun compareTo(other: Sequence): Int {
        return rank.compareTo(other.rank)
    }

    @Transient
    fun isNotStarted(): Boolean =
            state == State.beforeStart

    @Transient
    fun isStopped(): Boolean =
            state == State.afterStop

    @Transient
    fun executionIsFaceToFace(): Boolean =
            executionContext == ExecutionContext.FaceToFace

    @Transient
    fun executionIsBlended(): Boolean =
            executionContext == ExecutionContext.Blended

    @Transient
    fun executionIsDistance(): Boolean =
            executionContext == ExecutionContext.Distance

    @Transient
    fun resultsCanBePublished() =
            !resultsArePublished && (
                    isStopped() ||
                            (activeInteraction?.isRead() ?: false) ||
                            interactions[InteractionType.Evaluation]?.state == State.afterStop
                    )

    fun selectActiveInteraction(interactionType: InteractionType) {
        interactions[interactionType]?.let {
            activeInteraction = it
        } ?: throw IllegalStateException("No interaction ${interactionType} defined for this sequence")
    }

}

enum class State {
    beforeStart,
    show,
    afterStop
}

