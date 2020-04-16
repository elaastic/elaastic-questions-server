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

package org.elaastic.questions.lti

import org.elaastic.questions.test.TestingService
import org.elaastic.questions.test.directive.tGiven
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.persistence.EntityManager
import javax.transaction.Transactional
import org.hamcrest.MatcherAssert.assertThat

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
internal class LmsUserRepositoryIntegrationTest(
        @Autowired val entityManager: EntityManager,
        @Autowired val testingService: TestingService,
        @Autowired val lmsUserRepository: LmsUserRepository
) {

    @Test
    fun `test save of a valid lms user`() {
        tGiven {
            // a valid lms user
            LmsUser(
                    "lms user id",
                    testingService.getAnyLtiConsumer(),
                    testingService.getTestStudent()
            ).tWhen {
                // saving the lms user
                lmsUserRepository.saveAndFlush(it)
            }.tThen {
                entityManager.refresh(it)
                assertThat(it.id, notNullValue())
                assertThat(it.user, equalTo(testingService.getTestStudent()))
                assertThat(it.lmsUserId, equalTo("lms user id"))
                assertThat(it.lms, equalTo(testingService.getAnyLtiConsumer()))
            }
        }

    }


}
