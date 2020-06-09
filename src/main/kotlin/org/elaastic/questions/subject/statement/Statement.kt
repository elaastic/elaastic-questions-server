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

package org.elaastic.questions.subject.statement

import org.elaastic.questions.assignment.QuestionType
import org.elaastic.questions.assignment.choice.*
import org.elaastic.questions.attachment.Attachment
import org.elaastic.questions.directory.User
import org.elaastic.questions.persistence.AbstractJpaPersistable
import org.elaastic.questions.subject.Subject
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull


@Entity
@EntityListeners(AuditingEntityListener::class)
// TODO (+) We should define a interface so that a sequence can implement statement operations by delegation
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
        var expectedExplanation: String? = null,

        @field:ManyToOne( fetch = FetchType.LAZY)
        var subject: Subject? = null

) : AbstractJpaPersistable<Long>() {

    @Version
    var version: Long? = null

    @Column(name = "date_created")
    @CreatedDate
    lateinit var dateCreated: Date

    @LastModifiedDate
    @Column(name = "last_updated")
    var lastUpdated: Date? = null

    @OneToOne(mappedBy = "statement")
    var attachment: Attachment? = null

    @Transient
    fun isOpenEnded(): Boolean {
        return questionType == QuestionType.OpenEnded
    }

    @Transient
    fun isMultipleChoice(): Boolean {
        return questionType == QuestionType.MultipleChoice
    }

    @Transient
    fun isExclusiveChoice(): Boolean {
        return questionType == QuestionType.ExclusiveChoice
    }

    @Transient
    fun hasChoices() = isMultipleChoice() || isExclusiveChoice()

    fun title(value: String): Statement {
        this.title = value
        return this
    }

    fun content(value: String): Statement {
        this.content = value
        return this
    }

    fun expectedExplanation(value: String?): Statement {
        this.expectedExplanation = value
        return this
    }

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


