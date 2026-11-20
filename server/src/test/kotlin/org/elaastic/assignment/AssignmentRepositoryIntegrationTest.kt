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
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
internal class AssignmentRepositoryIntegrationTest(
    @Autowired val assignmentRepository: AssignmentRepository,
    @Autowired val integrationTestingService: IntegrationTestingService
) {

    @Test
    fun `save a valid assignment`() {
        // Given : an assignment
        Assignment(
            "My test assignment",
            integrationTestingService.getAnyUser(),
            UUID.randomUUID()
        ).let {
            // When saving it
            assignmentRepository.saveAndFlush(it).let {
                // Then
                MatcherAssert.assertThat(it.id, CoreMatchers.not(CoreMatchers.nullValue()))
                MatcherAssert.assertThat(it.dateCreated, CoreMatchers.not(CoreMatchers.nullValue()))
                MatcherAssert.assertThat(it.lastUpdated, CoreMatchers.not(CoreMatchers.nullValue()))
                MatcherAssert.assertThat(it.version, CoreMatchers.equalTo(0L))
            }
        }
    }
}