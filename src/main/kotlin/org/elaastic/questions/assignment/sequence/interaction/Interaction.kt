package org.elaastic.questions.assignment.sequence.interaction

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

/**
 * @author John Tranier
 */
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
        var sequence: Sequence
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

    @Enumerated(EnumType.STRING)
    var state: State = State.beforeStart


    @Convert(converter = InteractionResultConverter::class)
    var results: InteractionResult? = null

    @Convert(converter = ExplanationRecommendationMappingConverter::class)
    var explanationRecommendationMapping: ExplanationRecommendationMapping? = null

    // TODO Methods
}

