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

import org.elaastic.assignment.Assignment
import org.elaastic.sequence.FakeExplanationData
import org.elaastic.sequence.Sequence
import org.elaastic.sequence.SequenceRepository
import org.elaastic.analytics.lrs.EventLogRepository
import org.elaastic.questions.assignment.sequence.explanation.FakeExplanation
import org.elaastic.questions.assignment.sequence.explanation.FakeExplanationRepository
import org.elaastic.activity.response.ResponseRepository
import org.elaastic.questions.attachment.AttachmentService
import org.elaastic.user.User
import org.elaastic.questions.subject.Subject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class StatementService(
    @Autowired val attachmentService: AttachmentService,
    @Autowired val statementRepository: StatementRepository,
    @Autowired val eventLogRepository: EventLogRepository,
    @Autowired val sequenceRepository: SequenceRepository,
    @Autowired val responseRepository: ResponseRepository,
    @Autowired val fakeExplanationRepository: FakeExplanationRepository
) {
    fun get(user: User, id: Long): Statement {
        val statement:Statement = get(id)
        if (statement.owner != user && !user.isTeacher()) {
            throw AccessDeniedException("You are not authorized to access to this statement")
        }

        return statement
    }

    fun get(statementId: Long): Statement{
        return statementRepository.getReferenceById(statementId)
    }

    fun save(statement: Statement): Statement {
        return statementRepository.save(statement)
    }

    fun delete(statement: Statement) {
        removeAllFakeExplanation(statement)
        statementRepository.delete(statement)
    }

    fun addFakeExplanation(statement: Statement,
                           fakeExplanationData: FakeExplanationData
    ): FakeExplanation {
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
        removeAllFakeExplanation(statement)
        if(fakeExplanationDataList.isNotEmpty()) {
            fakeExplanationDataList.forEach {
                if(it.content?.isNotEmpty() == true) {
                    addFakeExplanation(statement, it)
                }
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

    fun findAllBySubject(subject: Subject): List<Statement> {
        return statementRepository.findBySubject(
                subject
        )
    }

    fun duplicate(statement: Statement): Statement {
        var duplicatedStatement =
                Statement(
                        statement.owner,
                        statement.title,
                        statement.content,
                        statement.questionType,
                        statement.choiceSpecification,
                        statement,
                        statement.expectedExplanation,
                        statement.subject
                )
        statement.attachment?.let { attachment ->
            attachmentService.duplicateAttachment(attachment).let { duplicatedAttachment ->
                attachmentService.addStatementToAttachment(duplicatedStatement, duplicatedAttachment)
            }
        }
        duplicatedStatement.rank = statement.rank
        duplicatedStatement.parentStatement = statement
        duplicatedStatement = save(duplicatedStatement)
        for (fakeExplanation: FakeExplanation in findAllFakeExplanationsForStatement(statement)){
            addFakeExplanation(
                    duplicatedStatement,
                    FakeExplanationData(fakeExplanation.correspondingItem, fakeExplanation.content)
            )
        }
        return statementRepository.save(duplicatedStatement)
    }

    fun assignStatementToSequences(newStatement: Statement) {
        val subject: Subject = newStatement.subject!!
        for (assignment: Assignment in subject.assignments){
            for (sequence: Sequence in assignment.sequences){
                if ((sequence.statement == newStatement.parentStatement)&&
                    (sequence.activeInteraction == null))
                    sequence.statement = newStatement
            }
        }
    }

    fun responsesExistForStatement(statement: Statement) =
            responseRepository.countByStatement(statement) > 0

    fun eventLogsExistForStatement(statement: Statement):Boolean {
        val sequences = sequenceRepository.findAllByStatement(statement)
        return eventLogRepository.countBySequenceIn(sequences) > 0
    }

}
