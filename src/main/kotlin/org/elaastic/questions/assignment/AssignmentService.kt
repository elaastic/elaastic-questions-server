package org.elaastic.questions.assignment

import org.elaastic.questions.assignment.sequence.SequenceRepository
import org.elaastic.questions.directory.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import javax.transaction.Transactional

/**
 * @author John Tranier
 */
@Service
@Transactional
class AssignmentService(
        @Autowired val assignmentRepository: AssignmentRepository,
        @Autowired val sequenceRepository: SequenceRepository
) {
    
    fun findAllByOwner(owner: User, pageable: Pageable): Page<Assignment> {
        return assignmentRepository.findAllByOwner(owner, pageable)
    }

    fun get(id: Long, fetchSequences: Boolean=false) : Assignment {
        assignmentRepository.getOne(id).let {

            it.sequences = sequenceRepository.findAllByAssignment(it, Sort.by("rank").ascending())

            return it
        }
    }
}