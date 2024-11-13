package org.elaastic.assignment

import org.elaastic.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface LearnerAssignmentRepository : JpaRepository<LearnerAssignment, Long> {

    fun findByLearnerAndAssignment(learner: User, assignment: Assignment): LearnerAssignment?

    @Query("select la.assignment from LearnerAssignment as la where la.learner = ?1 order by la.assignment.lastUpdated desc")
    fun findAllAssignmentsForLearnerWithoutPage(user: User): List<Assignment>

    fun countAllByAssignment(assignment: Assignment): Int

    // TODO: order by phase & name.
    fun findAllByAssignment(assignment: Assignment): List<LearnerAssignment>

    @Query("SELECT DISTINCT u from User u JOIN u.registrations la LEFT JOIN FETCH u.casUser WHERE la.assignment = ?1")
    fun findAllRegisteredLearnersOnAssignmentWithCasUser(assignment: Assignment): List<User>
}