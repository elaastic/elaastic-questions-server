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
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class UserLinkRepositoryIntegrationTest(
    @Autowired val userLinkRepository: UserLinkRepository,
    @Autowired val integrationTestingService: IntegrationTestingService
) {

    @Test
    fun `test findByUser when a userLink exist`() {
        val user = integrationTestingService.getAnyUser()
        val userLink = UserLink(
            providerId = "testProvider",
            providerUserId = user.username,
            user = user
        ).let(userLinkRepository::save)

        val foundUserLink = userLinkRepository.findByUser(user)

        assertNotNull(foundUserLink)
        foundUserLink!!
        assertEquals(userLink, foundUserLink)
        assertEquals(user, foundUserLink.user)
    }

    @Test
    fun `test findByUser when a userLink does not exist`() {
        val user = integrationTestingService.getAnyUser()

        val foundUserLink = userLinkRepository.findByUser(user)

        assertNull(foundUserLink)
    }
}