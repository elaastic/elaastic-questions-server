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

package org.elaastic.questions.assignment

import org.elaastic.questions.assignment.sequence.FakeExplanationData
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.SequenceRepository
import org.elaastic.questions.subject.statement.StatementService
import org.elaastic.questions.attachment.AttachmentService
import org.elaastic.questions.directory.User
import org.elaastic.questions.subject.Subject
import org.elaastic.questions.subject.statement.Statement
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import java.lang.IllegalStateException
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.EntityNotFoundException
import javax.transaction.Transactional


@Service
@Transactional
class AssignmentService(
        @Autowired val assignmentRepository: AssignmentRepository,
        @Autowired val sequenceRepository: SequenceRepository,
        @Autowired val learnerAssignmentRepository: LearnerAssignmentRepository,
        @Autowired val statementService: StatementService,
        @Autowired val attachmentService: AttachmentService,
        @Autowired val entityManager: EntityManager
) {

    fun findAllByOwner(owner: User,
                       pageable: Pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "lastUpdated")))
            : Page<Assignment> {
        return assignmentRepository.findAllByOwner(owner, pageable)
    }

    fun get(id: Long, fetchSequences: Boolean = false): Assignment {
        // TODO (+) i18n error message
        return when (fetchSequences) {
            true -> assignmentRepository.findOneWithSequencesById(id)
            false -> assignmentRepository.findOneById(id)
        } ?: throw EntityNotFoundException("There is no assignment for id \"$id\"")
    }

    fun get(user: User, id: Long, fetchSequences: Boolean = false): Assignment {
        get(id, fetchSequences).let {
            if (it.owner != user) {
                throw AccessDeniedException("You are not autorized to access to this assignment")
            }
            return it
        }
    }

    fun delete(user: User, assignment: Assignment) {
        require(user == assignment.owner) {
            "Only the owner can delete an assignment"
        }
        assignmentRepository.delete(assignment) // all other linked entities are deletes by DB cascade
    }

    fun save(assignment: Assignment): Assignment {
        return assignmentRepository.save(assignment)
    }

    fun count(): Long {
        return assignmentRepository.count()
    }

    fun countAllSequence(assignment: Assignment): Int {
        return sequenceRepository.countAllByAssignment(assignment)
    }

    fun touch(assignment: Assignment) {
        assignment.lastUpdated = Date()
        assignmentRepository.save(assignment)
    }

    fun addSequence(assignment: Assignment, statement: Statement): Sequence {
        val sequence = Sequence(
                owner = assignment.owner,
                statement = statement,
                rank = (assignment.sequences.map { it.rank }.max() ?: 0) + 1
        )

        assignment.addSequence(sequence)
        //statementService.save(sequence.statement)
        sequenceRepository.save(sequence)
        touch(assignment)

        return sequence
    }

    fun deleteStatementIfNotUsed(statement: Statement, assignment: Assignment) {
        var stillUsed = false
        for (sequence: Sequence in assignment.sequences) {
            if (statement == sequence.statement) stillUsed = true
        }

        if (!stillUsed) {
            statementService.delete(statement) // all other linked entities are deletes by DB cascade
            entityManager.flush()
            entityManager.clear()
        }
    }

    fun addStatementIfNotInAssignment(statement: Statement, assignment: Assignment) {
        var toAdd:Boolean = true
        for (sequence:Sequence in assignment.sequences){
            if (statement == sequence.statement) toAdd = false
        }
        if (toAdd) addSequence(assignment, statement)
    }

    fun removeSequence(user: User, sequence: Sequence) {
        require(user == sequence.owner) {
            "Only the owner can delete a sequence"
        }
        val assignment = sequence.assignment!!
        touch(assignment)
        assignment.sequences.remove(sequence)
        entityManager.flush()
        deleteStatementIfNotUsed(sequence.statement, assignment)
        sequenceRepository.delete(sequence) // all other linked entities are deletes by DB cascade
        entityManager.flush()
        entityManager.clear()
        updateAllSequenceRank(assignment)

    }

    fun moveUpSequence(assignment: Assignment, sequenceId: Long) {
        val idsArray = assignment.sequences.map { it.id }.toTypedArray()
        val pos = idsArray.indexOf(sequenceId)

        if (pos == -1)
            throw IllegalStateException("This sequence $sequenceId does not belong to assignment ${assignment.id}")
        if (pos == 0)
            return  // Nothing to do

        entityManager.createNativeQuery(
                "UPDATE sequence SET rank = CASE " +
                        "WHEN id=${sequenceId} THEN ${pos} " +
                        "WHEN id=${idsArray[pos - 1]} THEN ${pos + 1} " +
                        " END " +
                        "WHERE id in (${idsArray[pos - 1]}, ${sequenceId})"
        ).executeUpdate()
    }

    fun moveDownSequence(assignment: Assignment, sequenceId: Long) {
        val idsArray = assignment.sequences.map { it.id }.toTypedArray()
        val pos = idsArray.indexOf(sequenceId)

        if (pos == -1)
            throw IllegalStateException("This sequence $sequenceId does not belong to assignment ${assignment.id}")
        if (pos == assignment.sequences.size - 1)
            return  // Nothing to do

        entityManager.createNativeQuery(
                "UPDATE sequence SET rank = CASE " +
                        "WHEN id=${sequenceId} THEN ${pos + 1} " +
                        "WHEN id=${idsArray[pos + 1]} THEN ${pos} " +
                        " END " +
                        "WHERE id in (${idsArray[pos + 1]}, ${sequenceId})"
        ).executeUpdate()
    }

    fun updateAllSequenceRank(assignment: Assignment) {
        val sequenceIds = assignment.sequences.map { it.id }
        if (sequenceIds.isEmpty()) return // Nothing to do

        entityManager.createNativeQuery(
                "UPDATE sequence SET rank = CASE " +
                        sequenceIds.mapIndexed { index, id ->
                            "WHEN id=$id THEN $index"
                        }.joinToString(" ") +
                        " END " +
                        "WHERE id in (${sequenceIds.joinToString(",")})"
        ).executeUpdate()

        assignment.sequences.mapIndexed { index, sequence -> sequence.rank = index + 1 }
    }

    fun findAllAssignmentsForLearner(user: User,
                                     pageable: Pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "lastUpdated"))): Page<Assignment> {
        return learnerAssignmentRepository.findAllAssignmentsForLearner(user, pageable)
    }

    fun findByGlobalId(globalId: String): Assignment? {
        return assignmentRepository.findByGlobalId(globalId)
    }

    fun registerUser(user: User, assignment: Assignment): LearnerAssignment? {
        if (assignment.owner == user) {
            return null
        }

        return learnerAssignmentRepository.findByLearnerAndAssignment(
                user,
                assignment
        ) ?: learnerAssignmentRepository.save(
                LearnerAssignment(user, assignment)
        )
    }

    fun userIsRegisteredInAssignment(user: User, assignment: Assignment): Boolean {
        return learnerAssignmentRepository.findByLearnerAndAssignment(
                user,
                assignment
        ) != null
    }

    fun getNbRegisteredUsers(assignment: Assignment): Int {
        return learnerAssignmentRepository.countAllByAssignment(assignment)
    }

    fun getNbRegisteredUsers(assignmentId: Long): Int {
        return learnerAssignmentRepository.countAllByAssignment(
                assignmentRepository.getOne(assignmentId)
        )
    }

    fun buildFromSubject(assignment: Assignment, subject: Subject) {
        for (statement: Statement in subject.statements){
            this.addSequence(assignment,statement)
            touch(assignment)
        }
    }
}
