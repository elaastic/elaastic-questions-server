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

import javafx.beans.binding.Bindings.isNotNull
import org.elaastic.questions.assignment.Assignment
import org.elaastic.questions.assignment.sequence.FakeExplanationData
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.explanation.FakeExplanation
import org.elaastic.questions.assignment.sequence.explanation.FakeExplanationRepository
import org.elaastic.questions.directory.User
import org.elaastic.questions.subject.Subject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.Column
import javax.persistence.EntityNotFoundException
import javax.persistence.OneToOne
import javax.transaction.Transactional

@Service
@Transactional
class StatementService(
        @Autowired val statementRepository: StatementRepository,
        @Autowired val fakeExplanationRepository: FakeExplanationRepository
) {
    fun get(user: User, id: Long): Statement {
        val statement:Statement = get(id)

        if (statement.owner != user)
            if (!user.isTeacher()) {
                throw AccessDeniedException("You are not authorized to access to this statement")
            }

        return statement

    }

    fun get(statementId: Long): Statement{
        return statementRepository.getOne(statementId)
    }

    fun save(statement: Statement): Statement {
        return statementRepository.save(statement)
    }

    fun delete(statement: Statement) {
        removeAllFakeExplanation(statement)
        statementRepository.delete(statement)
    }

    fun addFakeExplanation(statement: Statement,
                           fakeExplanationData: FakeExplanationData): FakeExplanation {
        require(fakeExplanationData.content != null) {
            "The content of the fake explanation must not be null"
        }

        return fakeExplanationRepository.save(
                FakeExplanation(
                        author = statement.owner,
                        statement = statement,
                        content = fakeExplanationData.content!!,
                        correspondingItem = fakeExplanationData.correspondingItem
                )
        )

    }

    fun updateFakeExplanationList(statement: Statement,
                                  fakeExplanationDataList: List<FakeExplanationData>) {
        if(fakeExplanationDataList.isNotEmpty()) {
            removeAllFakeExplanation(statement)
            fakeExplanationDataList.forEach {
                addFakeExplanation(statement, it)
            }
        }
    }

    fun removeAllFakeExplanation(statement: Statement) {
        fakeExplanationRepository.deleteAllByStatement(statement)
    }

    fun findAllFakeExplanationsForStatement(statement: Statement): List<FakeExplanation> {
        return fakeExplanationRepository.findAllByStatement(
                statement
        )
    }

    fun duplicate(statement: Statement): Statement {
        var duplicatedStatement: Statement =
                Statement(
                        statement.owner,
                        statement.title,
                        statement.content,
                        statement.questionType,
                        statement.choiceSpecification,
                        statement,
                        statement.expectedExplanation,
                        statement.subject,
                        statement.rank
                )
        duplicatedStatement.version = statement.version
        duplicatedStatement.attachment = statement.attachment
        return statementRepository.save(duplicatedStatement)
    }

    fun assignStatementToSequences(newStatement: Statement) {
        val subject: Subject = newStatement.subject!!
        for (assignment: Assignment in subject.assignments){
            for (sequence: Sequence in assignment.sequences){
                if (sequence.statement == newStatement.parentStatement)
                    sequence.statement = newStatement
            }
        }
    }
}
