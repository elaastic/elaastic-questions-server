/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.elaastic.questions.assignment

import org.elaastic.questions.directory.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query


interface LearnerAssignmentRepository : JpaRepository<LearnerAssignment, Long> {

    fun findByLearnerAndAssignment(learner: User, assignment: Assignment): LearnerAssignment?

    @Query("select la.assignment from LearnerAssignment as la where la.learner = ?1 order by la.assignment.lastUpdated desc")
    fun findAllAssignmentsForLearnerWithoutPage(user: User): List<Assignment>

    fun countAllByAssignment(assignment: Assignment): Int

    fun findAllByAssignment(assignment: Assignment): List<LearnerAssignment>

    @Query("SELECT DISTINCT u from User u JOIN u.registrations la LEFT JOIN FETCH u.casUser WHERE la.assignment = ?1")
    fun findAllRegisteredLearnersOnAssignmentWithCasUser(assignment: Assignment): List<User>
}
