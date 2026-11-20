/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019-2026. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
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

package org.elaastic.assignment

import org.elaastic.test.IntegrationTestingService
import org.elaastic.test.directive.tThen
import org.elaastic.test.directive.tWhen
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.persistence.EntityManager
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class LearnerAssignmentRepositoryIntegrationTest(
    @Autowired val learnerAssignmentRepository: LearnerAssignmentRepository,
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val entityManager: EntityManager
) {

    @Test
    fun `save a valid learner assignment`() {
        val learner = integrationTestingService.getAnyUser()
        val assignment = integrationTestingService.getAnyAssignment()
        LearnerAssignment(
            learner = learner,
            assignment = assignment
        ).tWhen {
            learnerAssignmentRepository.saveAndFlush(it)
            entityManager.refresh(it)
            it
        }.tThen {
            MatcherAssert.assertThat(it.id, CoreMatchers.notNullValue())
            MatcherAssert.assertThat(it.version, CoreMatchers.equalTo(0L))
            MatcherAssert.assertThat(it.dateCreated, CoreMatchers.notNullValue())
            MatcherAssert.assertThat(it.lastUpdated, CoreMatchers.notNullValue())
            MatcherAssert.assertThat(it.learner, CoreMatchers.equalTo(learner))
            MatcherAssert.assertThat(it.assignment, CoreMatchers.equalTo(assignment))
        }
    }
}