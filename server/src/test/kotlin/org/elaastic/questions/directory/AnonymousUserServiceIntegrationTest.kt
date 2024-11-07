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

package org.elaastic.questions.directory

import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.elaastic.user.AnonymousUserService
import org.elaastic.user.RoleService
import org.elaastic.user.User
import org.elaastic.user.UserService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.transaction.Transactional
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.*


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
internal class AnonymousUserServiceIntegrationTest(
    @Autowired val anonymousUserService: AnonymousUserService,
    @Autowired val userService: UserService,
    @Autowired val roleService: RoleService
) {

    @Test
    fun authenticateAnonymousUser() {
        val nickname = "Joe"

        tWhen {
            // Adding an anonymous user
            anonymousUserService.authenticateAnonymousUser(nickname)
        }
            .tThen {
                assertEquals(1, it.authorities.size)
                assertEquals(roleService.roleStudent().authority, it.authorities.first().authority)
                assertNull(it.credentials)

                assertThat("The principal is not a User", it.principal is User)
                val user = it.principal as User
                assertEquals(nickname, user.firstName)
                assertTrue(user.isAnonymous())
                assertTrue(user.accountLocked)
            }
    }

}
