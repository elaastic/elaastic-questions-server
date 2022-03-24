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

package org.elaastic.questions.assignment.sequence.interaction

import org.elaastic.questions.assignment.ExecutionContext
import org.elaastic.questions.assignment.sequence.ExplanationRecommendationMappingConverter
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.assignment.sequence.interaction.results.ResponsesDistribution
import org.elaastic.questions.assignment.sequence.interaction.specification.InteractionSpecification
import org.elaastic.questions.assignment.sequence.interaction.specification.InteractionSpecificationConverter
import org.elaastic.questions.directory.User
import org.elaastic.questions.persistence.AbstractJpaPersistable
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull


@Entity
@EntityListeners(AuditingEntityListener::class)
class Interaction(
        @field:Enumerated(EnumType.STRING)
        var interactionType: InteractionType,

        var rank: Int,

        @field:Convert(converter = InteractionSpecificationConverter::class)
        var specification: InteractionSpecification? = null,

        @field:ManyToOne
        var owner: User,

        @field:OneToOne
        var sequence: Sequence,

        @field:Enumerated(EnumType.STRING)
        var state: State = State.beforeStart
) : AbstractJpaPersistable<Long>() {

    @Version
    var version: Long? = null

    @Column(name = "date_created")
    @CreatedDate
    lateinit var dateCreated: Date

    @LastModifiedDate
    @Column(name = "last_updated")
    var lastUpdated: Date? = null

    @Transient
    fun getStateForTeacher(user: User) =
            when (sequence.executionContext) {
                ExecutionContext.Distance -> State.afterStop
                ExecutionContext.Blended ->
                    if (interactionType == InteractionType.Read)
                        state
                    else State.afterStop
                ExecutionContext.FaceToFace -> state
            }

    @Convert(converter = ResponseDistributionConverter::class)
    var results: ResponsesDistribution? = null

    @Convert(converter = ExplanationRecommendationMappingConverter::class)
    var explanationRecommendationMapping: ExplanationRecommendationMapping? = null

    @Transient
    fun hasAnyResult(): Boolean =
            results?.hasAnyResult() ?: false

    @Transient
    fun isRead() = interactionType == InteractionType.Read

    @Transient
    fun isResponseSubmission() = interactionType == InteractionType.ResponseSubmission

    @Transient
    fun isEvaluation() = interactionType == InteractionType.Evaluation

    @Transient
    fun stateForRegisteredUsers(): State = when {
        isRead() && sequence.resultsArePublished -> State.show

        sequence.isStopped() ->
            if (rank <= sequence.activeInteraction?.rank ?: 0)
                state
            else State.beforeStart

        sequence.executionIsFaceToFace() -> state

        isRead() && sequence.executionIsBlended() -> state

        isRead() && sequence.executionIsDistance() -> State.afterStop

        else -> State.show
    }

    @Transient
    fun stateForLearner(learnerActiveInteraction: Interaction): State = when {
        sequence.isStopped() -> when {
            isRead() && sequence.resultsArePublished -> State.show
            rank <= sequence.activeInteraction!!.rank -> State.afterStop
            else -> State.beforeStart
        }

        !sequence.executionIsFaceToFace() && !sequence.isStopped() ->
            if (this == learnerActiveInteraction)
                when {
                    isRead() && sequence.resultsArePublished -> State.show
                    isRead() && sequence.executionIsBlended() -> state
                    else -> State.show
                }
            else State.afterStop

        else -> state
    }

}

