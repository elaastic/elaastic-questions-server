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
import org.elaastic.user.RoleService
import org.elaastic.user.User
import org.elaastic.user.UserService
import org.elaastic.user.UserSource
import org.jasig.cas.client.authentication.AttributePrincipal
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import javax.transaction.Transactional


@Service
class UserLinkService(
    @Autowired val userLinkRepository: UserLinkRepository,
    @Autowired val userService: UserService,
    @Autowired val roleService: RoleService,
) {
    fun loadUserByUsername(casKey: String, username: String): UserDetails? {
        return userLinkRepository.findByProviderIdAndProviderUserId(casKey, username)?.user?.also { it.casKey = casKey }
    }

    @Transactional
    fun registerNewCasUser(casKey: String, casProvider: String, principal: AttributePrincipal): UserDetails {
        val casAttributeParser = getCasAttributeParser(casProvider)
        val firstName = casAttributeParser.parseFirstName(principal)
        val lastName = casAttributeParser.parseLastName(principal)
        val email = casAttributeParser.parseEmail(principal)
        val roleId = casAttributeParser.parseRoleId(principal)

        val user = User(
            firstName = firstName,
            lastName = lastName,
            username = userService.generateUsername(firstName, lastName),
            plainTextPassword = userService.generatePassword(),
            email = email,
            source = UserSource.CAS,
        ).let {
            userService.addUser(
                it.addRole(roleService.roleForName(roleId.roleName, true)),
                "fr",
                checkEmailAccount = false,
                enable = true,
                addUserConsent = true
            )
        }

        UserLink(
            providerId = casKey,
            providerUserId = principal.name,
            user = user,
        ).let(userLinkRepository::save)

        return user
    }

    private fun getCasAttributeParser(casProvider: String): CasAttributeParser {
        return when (casProvider) {
            SupportedCasProvider.Kosmos.name -> CasAttributeParserForKosmos()
            SupportedCasProvider.Edifice.name -> CasAttributeParserForEdifice()
            else -> throw IllegalArgumentException("The CAS provider '$casProvider' is not supported")
        }
    }
}