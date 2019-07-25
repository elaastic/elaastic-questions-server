package org.elaastic.questions.assignment

import org.elaastic.questions.directory.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

/**
 * @author John Tranier
 */
@Service
@Transactional
class AssignmentService(
        @Autowired val assignmentRepository: AssignmentRepository
) {

    // TODO Handle pagination
    fun findAllByOwner(owner: User): List<Assignment> {
        return assignmentRepository.findAllByOwner(owner)
    }
}