package org.elaastic.questions.assignment

import org.elaastic.questions.directory.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
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
    
    fun findAllByOwner(owner: User, pageable: Pageable): Page<Assignment> {
        return assignmentRepository.findAllByOwner(owner, pageable)
    }
}