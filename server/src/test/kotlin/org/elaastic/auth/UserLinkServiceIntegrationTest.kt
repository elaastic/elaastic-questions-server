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

package org.elaastic.auth

import org.elaastic.test.IntegrationTestingService
import org.elaastic.user.Role
import org.elaastic.user.UserCreateCommand
import org.elaastic.user.UserRepository
import org.elaastic.user.UserSource
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
open class UserLinkServiceIntegrationTest(
    @Autowired val userLinkService: UserLinkService,
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val userLinkRepository: UserLinkRepository,
    @Autowired val userRepository: UserRepository,
) {


    @Test
    fun `test loadUserLinkByUsername`() {
        assertNull(userLinkService.loadUserLinkByUsername("", ""))
    }

    @Test
    fun `test loadUserLinkByUsername with a user in the database`() {
        val user = integrationTestingService.getAnyUser()
        val userLink = UserLink(
            providerId = "testProvider",
            providerUserId = user.username,
            user = user
        ).let(userLinkRepository::save)

        val loadedUser = userLinkService.loadUserLinkByUsername(userLink.providerId, userLink.providerUserId)

        assertEquals(userLink, loadedUser)
        assertEquals("testProvider", loadedUser?.user?.casKey)
        assertNull(userLinkService.loadUserLinkByUsername("anotherProvider", userLink.providerUserId))
    }

    @Test
    fun `test registerNewExternalUser`() {
        val providerId = "SomeProvider"
        val email = "john.doe@mail.com"
        val externalId = "external-subject-123"

        val userDetail = userLinkService.registerNewExternalUser(
            providerId,
            externalId,
            UserCreateCommand(
                "John",
                "Doe",
                email,
                Role.RoleId.TEACHER,
                UserSource.CAS,
                "fr"
            )
        )

        assertEquals("johdoe", userDetail.username)
        val userFound = userRepository.findUsersByEmailLike(email).first()
        assertNotNull(userFound)
        val userLinkFound = userLinkService.loadUserLinkByUsername(providerId, externalId)
        assertNotNull(userLinkFound)
        assertEquals(userDetail, userLinkFound?.user)
    }

}
