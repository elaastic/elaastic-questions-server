package org.elaastic.questions.assignment

import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.SequenceRepository
import org.elaastic.questions.assignment.sequence.StatementService
import org.elaastic.questions.directory.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.EntityNotFoundException
import javax.transaction.Transactional


@Service
@Transactional
class AssignmentService(
        @Autowired val assignmentRepository: AssignmentRepository,
        @Autowired val sequenceRepository: SequenceRepository,
        @Autowired val statementService: StatementService
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

    fun delete(user: User, id: Long) {
        if (assignmentRepository.deleteByIdAndOwner(id, user) != 1L) {
            throw EntityNotFoundException("There is no assignment \"$id\" for user ${user.username}")
        }
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

    fun removeSequence(sequence: Sequence) {
        require(sequence.assignment != null)
        val assignment = sequence.assignment!!
        // TODO Delete Attachement
        // TODO Delete PeerGrading
        // TODO Delete InteractionResponse
        // TODO Delete LearnerSequence

        statementService.delete(sequence.statement.id!!)
        assignment.sequences.remove(sequence)
        // TODO check that the sequence is deleted by cascade

        touch(assignment)
    }
}