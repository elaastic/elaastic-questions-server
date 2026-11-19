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

package org.elaastic.auth.cas

import org.elaastic.auth.UserLinkService
import org.elaastic.user.UserCreateCommand
import org.elaastic.user.UserSource
import org.jasig.cas.client.authentication.AttributePrincipal
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import java.util.logging.Logger

class CasAuthenticationUserDetailService(
    private val userLinkService: UserLinkService,
    private val casKey: String,
    private val casProvider: String,
) : AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {

    val logger: Logger = Logger.getLogger(CasAuthenticationUserDetailService::class.java.name)

    override fun loadUserDetails(token: CasAssertionAuthenticationToken): UserDetails {
        logger.finest {
            "token info: principal=${token.assertion.principal}, attributes=${token.assertion.principal.attributes}"
        }

        val username: String = token.name
        return userLinkService.loadUserLinkByUsername(casKey, username)?.user
            ?: userLinkService.registerNewExternalUser(
                casKey,
                token.assertion.principal.name,
                buildUserCreateCommand(token.assertion.principal)
            )
    }

    private fun buildUserCreateCommand(principal: AttributePrincipal): UserCreateCommand {
        val casAttributeParser = getCasAttributeParser(casProvider)

        val firstName = casAttributeParser.parseFirstName(principal)
        val lastName = casAttributeParser.parseLastName(principal)
        val email = casAttributeParser.parseEmail(principal)
        val roleId = casAttributeParser.parseRoleId(principal)

        return UserCreateCommand(firstName, lastName, email, roleId, UserSource.CAS, getLanguage(casProvider))
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
    private fun getLanguage(casProvider: String): String {
        return when (casProvider) {
            SupportedCasProvider.Kosmos.name -> "fr"
            SupportedCasProvider.Edifice.name -> "fr"
            else -> throw IllegalArgumentException("The CAS provider '$casProvider' is not supported")
        }
    }
}