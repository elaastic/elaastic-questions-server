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

package org.elaastic.sequence

import org.elaastic.test.IntegrationTestingService
import org.elaastic.test.directive.tThen
import org.elaastic.test.directive.tWhen
import org.springframework.boot.test.context.SpringBootTest
import javax.transaction.Transactional
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import javax.persistence.EntityManager



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
internal class SequenceRepositoryIntegrationTest(
    @Autowired val sequenceRepository: SequenceRepository,
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val entityManager: EntityManager
) {

    @Test
    fun `save a valid sequence`() {
        val owner = integrationTestingService.getAnyUser()
        val assignment = integrationTestingService.getAnyAssignment()
        val statement = integrationTestingService.getAnyStatement()
        Sequence(
            rank = 1,
            owner = owner,
            assignment = assignment,
            statement = statement,
            executionContext = ExecutionContext.Blended
        ).tWhen {
            sequenceRepository.saveAndFlush(it)
            entityManager.refresh(it)
            it
        }.tThen {
            assertThat(it.id, notNullValue())
            assertThat(it.version, equalTo(0L))
            assertThat(it.dateCreated, notNullValue())
            assertThat(it.lastUpdated, notNullValue())
            assertThat(it.rank, equalTo(1))
            assertThat(it.owner, equalTo(owner))
            assertThat(it.assignment, equalTo(assignment))
            assertThat(it.statement, equalTo(statement))
            assertThat(it.state, equalTo(State.beforeStart))
        }
    }
}
