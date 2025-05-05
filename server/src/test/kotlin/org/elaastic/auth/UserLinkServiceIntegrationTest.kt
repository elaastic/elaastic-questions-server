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

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.elaastic.auth.cas.SupportedCasProvider
import org.elaastic.auth.oauth.createOidcUser
import org.elaastic.test.IntegrationTestingService
import org.elaastic.user.Role.RoleId
import org.elaastic.user.User
import org.elaastic.user.UserRepository
import org.jasig.cas.client.authentication.AttributePrincipalImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.context.annotation.Profile
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@Profile("test")
class UserLinkServiceIntegrationTest(
    @Autowired val userLinkService: UserLinkService,
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val userLinkRepository: UserLinkRepository,
) {

    @SpyBean
    lateinit var userRepository: UserRepository


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
    }

    @Test
    fun `test registerNewCasUser`() {
        val casProvider = SupportedCasProvider.Kosmos
        val email = "john.doe@mail.com"
        val username = "johdoe"
        val casKey = "casKey"
        val userDetail = userLinkService.registerNewCasUser(
            casKey,
            casProvider.name,
            AttributePrincipalImpl(
                username,
                buildAttribute("John", "Doe", email, false, casProvider)
            )
        )

        assertEquals(username, userDetail.username)
        val userFound = userRepository.findUsersByEmailLike(email).first()
        assertNotNull(userFound)
        val userLinkFound = userLinkService.loadUserLinkByUsername(casKey, userFound.username)
        assertNotNull(userLinkFound)
        assertEquals(userDetail, userLinkFound?.user)
    }

    /**
     * Build the attribute for the [AttributePrincipalImpl] with the user information.
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

    @Test
    fun `test isUserLinked with a linked user`() {
        val user = integrationTestingService.getAnyUser()
        UserLink(
            providerId = "testProvider",
            providerUserId = user.username,
            user = user
        ).let(userLinkRepository::save)

        assertTrue(userLinkService.isLinked(user))
        assertFalse(userLinkService.isNotLinked(user))
    }

    @Test
    fun `test isUserLinked with a not linked user`() {
        val user = integrationTestingService.getAnyUser()

        assertFalse(userLinkService.isLinked(user))
        assertTrue(userLinkService.isNotLinked(user))
    }

    @Test
    fun `test updateUserWithOidcUser without change should't call save`() {
        val user = integrationTestingService.getAnyUser()
        val oidcUser = createOidcUser(user, listOf(RoleId.STUDENT.name))

        val updatedUser = userLinkService.updateUserWithOidcUser(user, oidcUser)

        assertEquals(oidcUser.givenName, updatedUser.firstName)
        assertEquals(oidcUser.familyName, updatedUser.lastName)
        assertEquals(oidcUser.email, updatedUser.email)

        verify(userRepository, never()).save(any<User>())
    }

    @Test
    fun `test updateUserWithOidcUser with change should update the user`() {
        val user = integrationTestingService.getTestTeacher()
        val anotherUser = User(
            firstName = "${user.firstName}_another",
            lastName = "${user.lastName}_another",
            username = ("another_${user.username}" + "0".repeat(31)).subSequence(0, 31).toString(),
            plainTextPassword = "1234",
            email = "another_${user.email}",
        )
        assertNotEquals(user, anotherUser)
        val oidcUser = createOidcUser(anotherUser, listOf(RoleId.STUDENT.name))
        assertNotEquals(oidcUser.givenName, user.firstName)
        assertNotEquals(oidcUser.familyName, user.lastName)
        assertNotEquals(oidcUser.email, user.email)

        val updatedUser = userLinkService.updateUserWithOidcUser(user, oidcUser)

        assertEquals(oidcUser.givenName, updatedUser.firstName)
        assertEquals(oidcUser.familyName, updatedUser.lastName)
        assertEquals(oidcUser.email, updatedUser.email)

        verify(userRepository, times(1)).save(any<User>())
    }

    @Test
    fun `test updateUserWithOidcUser with 1 change should update the user`() {
        val user = integrationTestingService.getTestTeacher()
        val anotherUser = User(
            firstName = user.firstName,
            lastName = user.lastName,
            username = ("another_${user.username}" + "0".repeat(31)).subSequence(0, 31).toString(),
            plainTextPassword = "1234",
            email = "another_${user.email}",
        )
        assertNotEquals(user, anotherUser)
        val oidcUser = createOidcUser(anotherUser, listOf(RoleId.STUDENT.name))
        assertEquals(oidcUser.givenName, user.firstName)
        assertEquals(oidcUser.familyName, user.lastName)
        assertNotEquals(oidcUser.email, user.email)

        val updatedUser = userLinkService.updateUserWithOidcUser(user, oidcUser)

        assertEquals(oidcUser.givenName, updatedUser.firstName)
        assertEquals(oidcUser.familyName, updatedUser.lastName)
        assertEquals(oidcUser.email, updatedUser.email)

        verify(userRepository, times(1)).save(any<User>())
    }
}