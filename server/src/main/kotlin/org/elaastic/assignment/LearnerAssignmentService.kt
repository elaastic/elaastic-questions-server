package org.elaastic.assignment

import org.elaastic.activity.response.Response
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

    fun isGraderRegisteredOnAssignment(grader: User, response: Response) =
        isRegistered(
            grader,
            response.interaction.sequence.assignment ?: error("The response is not bound to an assignment")
        )
}