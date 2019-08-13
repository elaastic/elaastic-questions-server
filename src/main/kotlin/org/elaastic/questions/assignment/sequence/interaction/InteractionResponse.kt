package org.elaastic.questions.assignment.sequence.interaction

import org.elaastic.questions.assignment.choice.ChoiceListSpecification
import org.elaastic.questions.assignment.choice.ChoiceListSpecificationConverter
import org.elaastic.questions.directory.User
import org.elaastic.questions.persistence.AbstractJpaPersistable
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull


@Entity
@Table(name = "choice_interaction_response")
@EntityListeners(AuditingEntityListener::class)
class InteractionResponse(

        @field:ManyToOne
        var learner: User,

        @field:ManyToOne
        var interaction: Interaction,

        var attempt: Int = 1,

        var explanation: String? = null,

        var confidenceDegree: Int? = null,

        var meanGrade: Float? = null,

        @field:Convert(converter = ChoiceListSpecificationConverter::class)
        var choiceListSpecification: ChoiceListSpecification? = null,

        var score: Float? = null


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
}