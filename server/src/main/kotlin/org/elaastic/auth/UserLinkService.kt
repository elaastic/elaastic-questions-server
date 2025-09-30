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
open class UserLinkService(
    private val userLinkRepository: UserLinkRepository,
    private val userService: UserService,
    private val roleService: RoleService,
) {

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
            ?.also {
                // TODO It is not consistent to inject providerId (which may designates an OIDC provider) into casKey ; casKey should probably be renamed to providerId
                it.user.casKey = providerId
            }
    }

    /**
     * Register a new external user (provided by a CAS, OID or another external authentication provider).
     *
     * Get the information about the user from the principal. Create the new user, save it and create the link between
     * the user and the CAS key.
     *
     * @see CasAttributeParser
     * @see AttributePrincipal
     * @see UserLink
     */
    @Transactional
    open fun registerNewExternalUser(providerId: String, providerUserId: String, userCreateCommand: UserCreateCommand): User {
        val user = createUser(userCreateCommand)

        UserLink(providerId, providerUserId, user,).let(userLinkRepository::save)

        return user
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
    private fun createUser(command: UserCreateCommand) = User(
        firstName = command.firstName,
        lastName = command.lastName,
        username = userService.generateUsername(command.firstName, command.lastName),
        plainTextPassword = userService.generatePassword(),
        email = command.email,
        source = command.userSource,
    ).let {
        userService.addUser(
            it.addRole(roleService.roleForName(command.roleId.roleName, true)),
            command.language,
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
}