/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.elaastic.questions.assignment.sequence

import org.elaastic.questions.assignment.Assignment
import org.elaastic.questions.assignment.ExecutionContext
import org.elaastic.questions.subject.statement.Statement
import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.assignment.sequence.interaction.InteractionType
import org.elaastic.questions.assignment.sequence.interaction.specification.EvaluationSpecification
import org.elaastic.questions.assignment.sequence.interaction.specification.ResponseSubmissionSpecification
import org.elaastic.questions.directory.User
import org.elaastic.questions.persistence.AbstractJpaPersistable
import org.elaastic.questions.player.phase.evaluation.EvaluationPhaseConfig
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

    @Column(name="`rank`")
    var rank: Int = 0,

    @Column(name = "phase_2_skipped")
    var phase2Skipped: Boolean = false,

    @field:Enumerated(EnumType.STRING)
    var executionContext: ExecutionContext = ExecutionContext.FaceToFace,

    @field:Enumerated(EnumType.STRING)
    var evaluationPhaseConfig: EvaluationPhaseConfig = EvaluationPhaseConfig.ALL_AT_ONCE,

    @field:OneToOne
    var activeInteraction: Interaction? = null,

    @field:Enumerated(EnumType.STRING)
    var state: State = State.beforeStart,

    var resultsArePublished: Boolean = false,

    var chatGptEvaluationEnabled: Boolean = false


) : AbstractJpaPersistable<Long>(),
    Comparable<Sequence>, SequenceProgress {

    @Version
    var version: Long? = null

    @field:NotNull
    @Column(columnDefinition = "BINARY(16)")
    var uuid: UUID = UUID.randomUUID()

    @Column(name = "date_created")
    @CreatedDate
    lateinit var dateCreated: Date

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

    fun getInteractionAt(rank: Int): Interaction {
        return interactions.values.find { it.rank == rank }
            ?: error("There is no interaction for rank $rank in this sequence")
    }

    @Transient
    fun getResponseSubmissionInteraction() =
        interactions[InteractionType.ResponseSubmission]
            ?: throw IllegalStateException("The response submission interaction is not initialized")

    @Transient
    fun responseSubmissionInteractionIsInitialized() =
        interactions[InteractionType.ResponseSubmission] != null

    @Transient
    fun getResponseSubmissionSpecification(): ResponseSubmissionSpecification =
        getResponseSubmissionInteraction().specification.let { specification ->
            when (specification) {
                null -> error("This interaction has no specification")
                is ResponseSubmissionSpecification -> specification
                else -> error("Expected a ResponseSubmissionSpecification but got a ${specification.javaClass}")
            }
        }

    @Transient
    fun getEvaluationSpecification(): EvaluationSpecification =
        getEvaluationInteraction().specification.let { specification ->
            when (specification) {
                null -> error("This interaction has no specification")
                is EvaluationSpecification -> specification
                else -> error("Expected an EvaluationSpecification but got a ${specification.javaClass}")
            }
        }

    @Transient
    fun getEvaluationInteraction() =
        interactions[InteractionType.Evaluation]
            ?: throw IllegalStateException("The evaluation interaction is not initialized")

    @Transient
    fun getReadInteraction() =
        interactions[InteractionType.Read]
            ?: throw IllegalStateException("The read interaction is not initialized")


    override fun compareTo(other: Sequence): Int {
        return rank.compareTo(other.rank)
    }

    @Transient
    override fun isNotStarted(): Boolean =
        state == State.beforeStart

    @Transient
    override fun hasStarted(): Boolean =
        state != State.beforeStart

    @Transient
    fun isStopped(): Boolean =
        state == State.afterStop

    @Transient
    override fun isInProgress(): Boolean =
        state == State.show


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
        } ?: throw IllegalStateException("No interaction $interactionType defined for this sequence")
    }


    @Transient
    fun isSecondAttemptAllowed() = true

    fun whichAttemptEvaluate() =
        if (executionIsFaceToFace()) 1 else 2

    fun recommendable(): Boolean =

        /* is face to face */
        executionContext == ExecutionContext.FaceToFace

                /* exclusive choice question */
                && statement.isExclusiveChoice()

                && getResponseSubmissionSpecification().studentsProvideExplanation

                && state == State.show

    fun recommendableAfterPhase1(): Boolean =
        recommendable() && activeInteraction?.state == State.afterStop && activeInteraction?.rank == 1

    fun recommendableAfterPhase2(): Boolean =
        recommendable() && ((activeInteraction?.state == State.afterStop && activeInteraction?.rank == 2)

                || (activeInteraction?.state == State.show && activeInteraction?.rank == 3)

                || (activeInteraction?.state == State.beforeStart && activeInteraction?.rank == 3))

}

enum class State {
    beforeStart,
    show,
    afterStop
}

