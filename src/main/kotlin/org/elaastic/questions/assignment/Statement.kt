package org.elaastic.questions.assignment

import org.elaastic.questions.directory.User
import org.elaastic.questions.persistence.AbstractJpaPersistable
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

/**
 * @author John Tranier
 */
@Entity
@EntityListeners(AuditingEntityListener::class)
class Statement(
        @field:NotNull
        @field:ManyToOne
        var owner: User,

        @field:NotBlank
        var title: String,

        @field:NotBlank
        var content: String,

        @field:NotNull
        @Enumerated(EnumType.STRING)
        val questionType: QuestionType,

        @Convert(converter = ChoiceSpecificationConverter::class)
        @field:Column(name = "choice_specification")
        var choiceSpecification: ChoiceSpecification? = null,

        @field:ManyToOne
        var parentStatement: Statement? = null,

        @field:Column(name = "expected_explanation")
        var expectedExplanation: String? = null
        
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

    // TODO getFakeExplanations
    // TODO getAttachment

    fun isOpenEnded(): Boolean {
        return questionType == QuestionType.OpenEnded
    }

    fun isMultipleChoice(): Boolean {
        return questionType == QuestionType.MultipleChoice
    }

    fun isExclusiveChoice(): Boolean {
        return questionType == QuestionType.ExclusiveChoice
    }

    fun hasChoice(): Boolean {
        return isMultipleChoice() || isExclusiveChoice()
    }
}


