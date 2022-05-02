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

import org.elaastic.questions.directory.UserService
import org.elaastic.questions.test.IntegrationTestingService
import org.elaastic.questions.test.directive.tExpect
import org.elaastic.questions.test.directive.tGiven
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.logging.Logger
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
internal class LmsUserAccountCreationServiceIntegrationTest(
        @Autowired val lmsUserAccountCreationService: LmsUserAccountCreationService,
        @Autowired val userService: UserService,
        @Autowired val integrationTestingService: IntegrationTestingService
) {

    internal var logger = Logger.getLogger(LmsUserAccountCreationServiceIntegrationTest::class.java.name)

    @Test
    fun `test generate password`() {
        tWhen {
            userService.generatePassword()
        }.tExpect {
            // expect generated password is OK
            logger.info("generated password: $it")
            assertThat(it.length, equalTo(8))
            it
        }.tExpect {
            // 2 generated password are not identical
            val p2 = userService.generatePassword()
            logger.info("Second generated password: $p2")
            assertThat(it, not(equalTo(p2)))
        }
    }

   @Test
   fun testCreateUserFromLtiData() {
       tGiven {
            integrationTestingService.getLtiLaunchDataComingFromBoBDeniroTeacher().toLtiUser()
       }.tWhen {
           lmsUserAccountCreationService.createUserFromLtiData(it)
       }.tThen {
           assertThat(it.id, notNullValue())
           assertThat(it.firstName, equalTo("Bob"))
           assertThat(it.lastName, equalTo("Deniro"))
           assertTrue(it.isTeacher())
       }
   }


}
