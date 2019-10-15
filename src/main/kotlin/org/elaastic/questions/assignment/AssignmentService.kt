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
import org.elaastic.questions.assignment.sequence.StatementService
import org.elaastic.questions.attachment.AttachmentService
import org.elaastic.questions.directory.User
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
        // TODO i18n error message
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
        entityManager.createQuery("delete from LmsAssignment la where la.assignment = :assignment")
                .setParameter("assignment", assignment)
                .executeUpdate()

        assignment.sequences.forEach {
            removeSequence(it)
        }

        assignmentRepository.delete(assignment)
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
        statementService.save(sequence.statement)
        sequenceRepository.save(sequence)
        touch(assignment)

        return sequence
    }

    private fun removeSequence(sequence: Sequence) {

        entityManager.createNativeQuery("""
            DELETE pg
            FROM  peer_grading pg
                INNER JOIN choice_interaction_response cir on pg.response_id = cir.id
                INNER JOIN interaction i on cir.interaction_id = i.id
                INNER JOIN sequence s on i.sequence_id = s.id
            WHERE s.id = :sequenceId
        """.trimIndent())
                .setParameter("sequenceId", sequence.id)
                .executeUpdate()

        entityManager.createNativeQuery("""
            DELETE cir
            FROM choice_interaction_response cir
                     INNER JOIN interaction i on cir.interaction_id = i.id
                     INNER JOIN sequence s on i.sequence_id = s.id
            WHERE s.id = :sequenceId
        """.trimIndent())
                .setParameter("sequenceId", sequence.id)
                .executeUpdate()

        entityManager.createQuery("delete from LearnerSequence ls where ls.sequence = :sequence")
                .setParameter("sequence", sequence)
                .executeUpdate()
        entityManager.createQuery("delete from Interaction i where i.sequence = :sequence")
                .setParameter("sequence", sequence)
                .executeUpdate()
        sequence.statement.attachment?.let {
            attachmentService.detachAttachmentFromStatement(sequence.owner, sequence.statement)
        }

        statementService.delete(sequence.statement)

    }

    fun removeSequence(user: User, sequence: Sequence) {
        require(user == sequence.owner) {
            "Only the owner can delete a sequence"
        }
        val assignment = sequence.assignment!!
        removeSequence(sequence)
        assignment.sequences.remove(sequence)
        updateAllSequenceRank(assignment)
        touch(assignment)
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

    /**
     * Duplicate a assignment (create a copy of it, generating a new title, and assigning a
     * new owner to the copy)
     * @param user the user duplicating the assignment
     * @param assignment the course to duplicate
     * @return the duplicated course
     */
    fun duplicate(assignment: Assignment, user: User): Assignment {
        if (assignment.owner != user) {
            throw AccessDeniedException("You are not autorized to access to this assignment")
        }
        Assignment(
                title = assignment.title + "-copy",
                owner = assignment.owner
        ).let { duplicatedAssignment ->
            save(duplicatedAssignment)
            assignment.sequences.forEach { sequence ->
                duplicateSequenceInAssignment(sequence, duplicatedAssignment, user)
            }
            return duplicatedAssignment
        }
    }

    /**
     * Duplicate a sequence in an assignment (without interactions)
     * @param sequence the sequence to duplicate
     * @param duplicatedAssignment the target assignment
     * @param user the user performing the operation
     * @return the duplicated sequence
     */
    fun duplicateSequenceInAssignment(sequence: Sequence, duplicatedAssignment: Assignment, user: User): Sequence {
        if (duplicatedAssignment.owner != user) {
            throw AccessDeniedException("You are not autorized to access to this assignment")
        }
        with(sequence.statement) {
            Statement(
                    title = this.title,
                    content = this.content,
                    choiceSpecification = this.choiceSpecification,
                    questionType = this.questionType,
                    owner = this.owner,
                    parentStatement = this,
                    expectedExplanation = this.expectedExplanation
            ).let { duplicatedStatement ->
                this.attachment?.let { attachment ->
                    attachmentService.duplicateAttachment(attachment).let { duplicatedAttachment ->
                        attachmentService.addStatementToAttachment(duplicatedStatement, duplicatedAttachment)
                    }
                }
                addSequence(duplicatedAssignment, duplicatedStatement).let {
                    statementService.findAllFakeExplanationsForStatement(this).forEach { fakeExplanation ->
                        statementService.addFakeExplanation(duplicatedStatement, FakeExplanationData(
                                fakeExplanation
                        ))
                    }
                    return it
                }
            }
        }
    }
}
