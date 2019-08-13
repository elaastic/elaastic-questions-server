package org.elaastic.questions.assignment

import org.elaastic.questions.directory.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import javax.persistence.EntityNotFoundException
import javax.transaction.Transactional


@Service
@Transactional
class AssignmentService(
        @Autowired val assignmentRepository: AssignmentRepository
) {

    fun findAllByOwner(owner: User,
                       pageable: Pageable = PageRequest.of(0, 10))
            : Page<Assignment> {
        return assignmentRepository.findAllByOwner(owner, pageable)
    }

    fun get(id: Long, fetchSequences: Boolean = false): Assignment {
        return when (fetchSequences) {
            true -> assignmentRepository.findOneWithSequencesById(id)
            false -> assignmentRepository.findOneById(id)
        } ?: throw EntityNotFoundException("This is no assignment for id \"$id\"")
    }

    fun save(assignment: Assignment) : Assignment {
        return assignmentRepository.save(assignment)
    }
}