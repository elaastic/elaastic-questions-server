package org.elaastic.assignment

import org.elaastic.user.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class LearnerAssignmentService(
        @Autowired val learnerAssignmentRepository: LearnerAssignmentRepository
) {

    fun isRegistered(learner: User, assignment: Assignment): Boolean =
            learnerAssignmentRepository.findByLearnerAndAssignment(learner, assignment) != null
}