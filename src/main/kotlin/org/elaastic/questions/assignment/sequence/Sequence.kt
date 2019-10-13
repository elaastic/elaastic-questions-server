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
import org.elaastic.questions.assignment.Statement
import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.assignment.sequence.interaction.InteractionType
import org.elaastic.questions.assignment.sequence.interaction.specification.EvaluationSpecification
import org.elaastic.questions.assignment.sequence.interaction.specification.ResponseSubmissionSpecification
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

    fun getInteractionAt(rank: Int): Interaction {
        return interactions.values?.find { it.rank == rank }
                ?: error("There is no interaction for rank $rank in this sequence")
    }

    @Transient
    fun getResponseSubmissionInteraction() =
            interactions[InteractionType.ResponseSubmission]
                    ?: throw IllegalStateException("The response submission interaction is not initialized")

    @Transient
    fun getResponseSubmisssionSpecification(): ResponseSubmissionSpecification =
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


    @Transient
    fun isSecondAttemptAllowed() =
            !(executionIsFaceToFace() && statement.isOpenEnded())

    fun whichAttemptEvaluate() =
            if (executionIsFaceToFace()) 1 else 2

}

enum class State {
    beforeStart,
    show,
    afterStop
}

