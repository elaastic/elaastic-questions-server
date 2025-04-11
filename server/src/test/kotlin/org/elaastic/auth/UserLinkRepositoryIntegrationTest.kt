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