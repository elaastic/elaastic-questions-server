package org.elaastic.questions.assignment

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

        // TODO Delete PeerGrading
        // TODO Delete InteractionResponse

        entityManager.createQuery("delete from LearnerSequence ls where ls.sequence = :sequence")
                .setParameter("sequence", sequence)
                .executeUpdate()
        sequence.statement.attachment?.let {
            attachmentService.detachAttachmentFromStatement(sequence.owner, sequence.statement)
        }

        statementService.delete(sequence.statement.id!!)
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
        if(sequenceIds.isEmpty()) return // Nothing to do

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
                                     pageable: Pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "lastUpdated"))) : Page<Assignment> {
        return learnerAssignmentRepository.findAllAssignmentsForLearner(user, pageable)
    }

    fun findByGlobalId(globalId: String) : Assignment? {
        return assignmentRepository.findByGlobalId(globalId)
    }

    fun registerUser(user: User, assignment: Assignment) : LearnerAssignment? {
        if(assignment.owner == user) {
            return null
        }

        return learnerAssignmentRepository.findByLearnerAndAssignment(
                user,
                assignment
        ) ?: learnerAssignmentRepository.save(
                LearnerAssignment(user, assignment)
        )
    }

    fun getNbRegisteredUsers(assignment: Assignment): Int {
        return learnerAssignmentRepository.countAllByAssignment(assignment)
    }
}