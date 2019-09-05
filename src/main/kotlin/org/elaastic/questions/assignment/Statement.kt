package org.elaastic.questions.assignment

import org.elaastic.questions.assignment.choice.*
import org.elaastic.questions.attachement.Attachment
import org.elaastic.questions.directory.User
import org.elaastic.questions.persistence.AbstractJpaPersistable
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull


@Entity
@EntityListeners(AuditingEntityListener::class)
class Statement(
        @field:NotNull
        @field:ManyToOne(fetch = FetchType.LAZY)
        var owner: User,

        @field:NotBlank
        var title: String = "",

        @field:NotBlank
        var content: String = "",

        @field:NotNull
        @Enumerated(EnumType.STRING)
        var questionType: QuestionType,

        @Convert(converter = ChoiceSpecificationConverter::class)
        @field:Column(name = "choice_specification")
        var choiceSpecification: ChoiceSpecification? = null,

        @field:ManyToOne(fetch = FetchType.LAZY)
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

    fun getAttachment(): Attachment? {
        return null // TODO Implement getAttachement
    }

    fun isOpenEnded(): Boolean {
        return questionType == QuestionType.OpenEnded
    }

    fun isMultipleChoice(): Boolean {
        return questionType == QuestionType.MultipleChoice
    }

    fun isExclusiveChoice(): Boolean {
        return questionType == QuestionType.ExclusiveChoice
    }

    fun title(value: String): Statement {
        this.title = value
        return this
    }

    fun content(value: String): Statement {
        this.content = value
        return this
    }

    // TODO test
    fun updateFrom(otherStatement: Statement): Statement {
        require(id == otherStatement.id)
        require(owner == otherStatement.owner)
        if (version != otherStatement.version) {
            throw OptimisticLockException()
        }

        title = otherStatement.title
        content = otherStatement.content
        questionType = otherStatement.questionType
        choiceSpecification = otherStatement.choiceSpecification
        parentStatement = otherStatement.parentStatement
        expectedExplanation = otherStatement.expectedExplanation

        return this
    }

    companion object {
        fun createDefaultStatement(user: User): Statement {
            return Statement(
                    owner = user,
                    questionType = QuestionType.ExclusiveChoice,
                    choiceSpecification = ExclusiveChoiceSpecification(
                            nbCandidateItem = 2,
                            expectedChoice = ChoiceItem(1, 1f)
                    )
            )
        }
    }
}


