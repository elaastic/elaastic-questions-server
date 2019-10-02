package org.elaastic.questions.assignment.sequence.interaction

import org.elaastic.questions.assignment.ExecutionContext
import org.elaastic.questions.assignment.sequence.ExplanationRecommendationMappingConverter
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.State
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

    @NotNull
    @Column(name = "date_created")
    @CreatedDate
    lateinit var dateCreated: Date

    @NotNull
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

    @Convert(converter = InteractionResultConverter::class)
    var results: InteractionResult? = null

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
                State.afterStop
            else State.beforeStart

        sequence.executionIsFaceToFace() -> state

        isRead() && sequence.executionIsBlended() -> state

        isRead() && sequence.executionIsDistance() -> State.afterStop

        else -> State.show
    }
}

