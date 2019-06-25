package org.elaastic.questions.server

import org.elaastic.questions.server.persistence.AbstractJpaPersistable
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

/**
 * @author John Tranier
 */
@Entity
class Statement(
        @NotBlank
        val title: String,

        @NotBlank
        val content: String,

        @Column(name = "expected_explanation")
        val expectedExplanation: String,

        @Column(name = "choice_specification")
        val choiceSpecification: String,

        val questionType: String,

        @ManyToOne
        val parentStatement: Statement?,

        @NotNull
        @Column(name = "date_created")
        val dateCreated: Date,

        @NotNull
        @Column(name = "last_updated")
        val lastUpdated: Date

        // TODO User
        // TODO Attachments
        
) : AbstractJpaPersistable<Long>() {

    // TODO getFakeExplanations
    // TODO getChoiceSpecificationObject
    // TODO getAttachment

    fun isOpenEnded(): Boolean {
        return questionType == QuestionType.OpenEnded.toString()
    }

    fun isMultipleChoice(): Boolean {
        return questionType == QuestionType.MultipleChoice.toString()
    }

    fun isExclusiveChoice(): Boolean {
        return questionType == QuestionType.ExclusiveChoice.toString()
    }

    fun hasChoice(): Boolean {
        return isMultipleChoice() || isExclusiveChoice()
    }

}


