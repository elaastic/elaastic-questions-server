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

import org.elaastic.auth.cas.CasAttributeParser
import org.elaastic.auth.cas.CasAttributeParserForEdifice
import org.elaastic.auth.cas.CasAttributeParserForKosmos
import org.elaastic.auth.cas.SupportedCasProvider
import org.elaastic.user.*
import org.jasig.cas.client.authentication.AttributePrincipal
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional

/**
 * Service to manage the link between Elaastic user and external authentication providers, like CAS or OIDC.
 *
 * @see org.elaastic.auth.cas.CasAuthenticationUserDetailService
 * @see org.elaastic.auth.oauth.ElaasticOidcUserService
 */
@Service
class UserLinkService(
    @Autowired val userLinkRepository: UserLinkRepository,
    @Autowired val userService: UserService,
    @Autowired val roleService: RoleService,
    @Autowired val userRepository: UserRepository,
) {

    @Value("\${spring.security.oauth2.client.registration.keycloak.provider}")
    val oidcProvider: String = "oidcProvider default value"

    /**
     * Fetch the userLink for the given providerId and username.
     *
     * If it's found, the user is updated with the providerId.
     *
     * @param providerId The provider id
     * @param username The username of the user
     * @see UserLink
     */
    fun loadUserLinkByUsername(providerId: String, username: String): UserLink? {
        return userLinkRepository.findByProviderIdAndProviderUserId(providerId, username)
            ?.also { it.user.casKey = providerId }
    }

    /**
     * Register a new user with the given CAS provider.
     *
     * Get the information about the user from the principal. Create the new user, save it and create the link between
     * the user and the CAS key.
     *
     * @see CasAttributeParser
     * @see AttributePrincipal
     * @see UserLink
     */
    @Transactional
    fun registerNewCasUser(casKey: String, casProvider: String, principal: AttributePrincipal): User {
        val casAttributeParser = getCasAttributeParser(casProvider)
        val firstName = casAttributeParser.parseFirstName(principal)
        val lastName = casAttributeParser.parseLastName(principal)
        val email = casAttributeParser.parseEmail(principal)
        val roleId = casAttributeParser.parseRoleId(principal)

        val user = createUser(
            firstName,
            lastName,
            email,
            roleId,
            UserSource.CAS,
            getLanguage(casProvider)
        )

        UserLink(
            providerId = casKey,
            providerUserId = principal.name,
            user = user,
        ).let(userLinkRepository::save)

        return user
    }

    /** Register a new user with the given OIDC user and role */
    @Transactional
    fun registerNewOidcUser(oidcUser: OidcUser, role: Role.RoleId): User {
        val user = createUser(
            oidcUser.givenName,
            oidcUser.familyName,
            oidcUser.email,
            role,
            UserSource.OIDC,
            getLanguage(oidcUser, "fr")
        )

        UserLink(
            providerId = this.oidcProvider,
            providerUserId = oidcUser.name,
            user = user
        ).let(userLinkRepository::save)

        return user
    }

    /**
     * Return the [CasAttributeParser] for the given CAS provider.
     *
     * @throws IllegalArgumentException if the CAS provider is not supported
     */
    private fun getCasAttributeParser(casProvider: String): CasAttributeParser {
        return when (casProvider) {
            SupportedCasProvider.Kosmos.name -> CasAttributeParserForKosmos()
            SupportedCasProvider.Edifice.name -> CasAttributeParserForEdifice()
            else -> throw IllegalArgumentException("The CAS provider '$casProvider' is not supported")
        }
    }

    /**
     * Return the language of the CAS provider.
     *
     * As the user is managed by the CAS provider, we assume that his language and the Cas provider's language are the
     * same.
     */
    // TODO maybe store the language information of the CAS in another class
    private fun getLanguage(casProvider: String): String {
        return when (casProvider) {
            SupportedCasProvider.Kosmos.name -> "fr"
            SupportedCasProvider.Edifice.name -> "fr"
            else -> throw IllegalArgumentException("The CAS provider '$casProvider' is not supported")
        }
    }

    /**
     * Return the language of the user.
     *
     * See [OIDC Standard claims documentation](https://openid.net/specs/openid-connect-core-1_0.html#StandardClaims)
     * for more information about the locale.
     *
     * If no locale is found, the default is returned.
     *
     * The default is given in the parameter, or it's the JVM default locale.
     *
     * @param oidcUser The OIDC user to get the locale from
     * @param default The default language to use if no locale is found
     * @see OidcUser.getLocale
     * @see Locale.getDefault
     */
    private fun getLanguage(oidcUser: OidcUser, default: String = Locale.getDefault().language): String {
        val localeClaim = oidcUser.locale

        return if (!localeClaim.isNullOrBlank()) {
            Locale.forLanguageTag(localeClaim).language
        } else {
            default
        }
    }

    /**
     * Create a new user with the given information.
     *
     * The user is created with the given first name, last name, email, role, source and language. The user service
     * generates the password and the username.
     *
     * As the user is created with an authentication provider, the user is enabled and his consent is added.
     *
     * Then the user is saved and returned.
     */
    private fun createUser(
        firstName: String,
        lastName: String,
        email: String?,
        roleId: Role.RoleId,
        userSource: UserSource,
        language: String
    ) = User(
        firstName = firstName,
        lastName = lastName,
        username = userService.generateUsername(firstName, lastName),
        plainTextPassword = userService.generatePassword(),
        email = email,
        source = userSource,
    ).let {
        userService.addUser(
            it.addRole(roleService.roleForName(roleId.roleName, true)),
            language,
            checkEmailAccount = false,
            enable = true,
            addUserConsent = true
        )
    }

    /** @return true if the user is linked to an external authentication provider, false otherwise */
    fun isLinked(user: User): Boolean {
        return userLinkRepository.findByUser(user) != null
    }

    /**
     * This method is the inverse of [isLinked].
     *
     * @return true if the user is not linked to an external authentication provider, false otherwise
     * @see isLinked
     */
    fun isNotLinked(user: User): Boolean {
        return !isLinked(user)
    }

    /**
     * Update the user with the given OIDC user
     *
     * The user information are updated with the OIDC user information.
     */
    fun updateUserWithOidcUser(user: User, oidcUser: OidcUser): User {
        return if (user.firstName != oidcUser.givenName || user.lastName != oidcUser.familyName || user.email != oidcUser.email) {
            user.apply {
                firstName = oidcUser.givenName
                lastName = oidcUser.familyName
                email = oidcUser.email
            }.let(userRepository::save)
        } else {
            user
        }
    }
}