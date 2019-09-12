package org.elaastic.questions.assignment

import org.elaastic.questions.directory.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query


interface LearnerAssignmentRepository : JpaRepository<LearnerAssignment, Long> {

    fun findByLearnerAndAssignment(learner: User, assignment: Assignment): LearnerAssignment?

    @Query("select la.assignment from LearnerAssignment as la where la.learner = ?1")
    fun findAllAssignmentsForLearner(user: User,
                                     pageable: Pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "lastUpdated"))): Page<Assignment>
}