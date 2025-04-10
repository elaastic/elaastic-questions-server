package org.elaastic.auth

import org.elaastic.auth.cas.SupportedCasProvider
import org.elaastic.test.IntegrationTestingService
import org.elaastic.user.UserRepository
import org.jasig.cas.client.authentication.AttributePrincipalImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Profile
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@Profile("test")
class UserLinkServiceIntegrationTest(
    @Autowired val userLinkService: UserLinkService,
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val userLinkRepository: UserLinkRepository,
    @Autowired val userRepository: UserRepository,
) {


    @Test
    fun `test loadUserByUsername`() {
        assertNull(userLinkService.loadUserByUsername("", ""))
    }

    @Test
    fun `test loadUserByUsername with a user in the database`() {
        val user = integrationTestingService.getAnyUser()
        val userLink = UserLink(
            providerId = "testProvider",
            providerUserId = user.username,
            user = user
        ).let(userLinkRepository::save)

        val loadedUser = userLinkService.loadUserByUsername(userLink.providerId, userLink.providerUserId)

        assertEquals(userLink, loadedUser)
    }

    @Test
    fun `test registerNewCasUser`() {
        val casProvider = SupportedCasProvider.Kosmos
        val email = "john.doe@mail.com"
        val providerId = "johdoe"
        val userDetail = userLinkService.registerNewCasUser(
            "casKey",
            casProvider.name,
            AttributePrincipalImpl(
                providerId,
                buildAttribute("John", "Doe", email, false, casProvider)
            )
        )

        assertEquals(providerId, userDetail.username)
        val userFound = userRepository.findUsersByEmailLike(email).first()
        assertNotNull(userFound)
        val userLinkFound = userLinkService.loadUserByUsername(providerId, userFound.username)
        assertNotNull(userLinkFound)
        assertEquals(userDetail, userLinkFound?.user)
    }

    /**
     * Build the attribute for the [AttributePrincipalImpl] with the user
     * information.
     *
     * The information depends on the CAS provider.
     */
    private fun buildAttribute(
        firstName: String,
        lastName: String,
        email: String,
        isTeacher: Boolean,
        casProvider: SupportedCasProvider
    ): Map<String, String> {
        return when (casProvider) {
            SupportedCasProvider.Kosmos -> mapOf(
                "prenom" to firstName,
                "nom" to lastName,
                "mail" to email,
                "profil" to if (isTeacher) "Professeur" else "Eleve"
            )

            SupportedCasProvider.Edifice -> TODO("Specify the map attribute for Edifice")
        }
    }

}