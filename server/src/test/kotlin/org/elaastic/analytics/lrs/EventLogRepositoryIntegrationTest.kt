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

package org.elaastic.analytics.lrs

import org.elaastic.analytics.lrs.Action
import org.elaastic.analytics.lrs.EventLog
import org.elaastic.analytics.lrs.EventLogRepository
import org.elaastic.analytics.lrs.ObjectOfAction
import org.elaastic.user.Role
import org.elaastic.test.IntegrationTestingService
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.transaction.Transactional


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class EventLogRepositoryIntegrationTest(
    @Autowired val eventLogRepository: EventLogRepository,
    @Autowired val integrationTestingService: IntegrationTestingService
) {

    @Test
    fun `save a valid action`() {

        val sequence = integrationTestingService.getAnySequence()
        val user = integrationTestingService.getAnyUser()

        // Given : an eventLog
        EventLog(
                sequence,
                user,
                Role.RoleId.STUDENT,
                Action.OPEN,
                ObjectOfAction.EXPLANATION_POPUP
        ).let {
            // When saving it
            eventLogRepository.saveAndFlush(it).let {
                // Then
                assertThat(it.id, not(nullValue()))
                assertThat(it.date, not(nullValue()))
            }
        }
    }
}
