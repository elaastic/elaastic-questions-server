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
package org.elaastic.auth.oauth

import org.elaastic.auth.UserLinkService
import org.elaastic.user.Role
import org.elaastic.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Service

/**
 * Service dedicated to retrieve the Elaastic User bound to an OidcUser
 *
 * Implementation note: This service overrides OidcUserService, and, as such, must return an OidcUser. The
 * ElaasticOidcUser is an OidcUser bound to its corresponding Elaastic User
 *
 * @author John Tranier
 */
@Service
class ElaasticOidcUserService(
    @Autowired val userRepository: UserRepository,
    private val userLinkService: UserLinkService
) : OidcUserService() {

    override fun loadUser(userRequest: OidcUserRequest?): OidcUser {
        val oidcUser = super.loadUser(userRequest)

        val role = Role.RoleId.STUDENT //STUB
        val user = userLinkService.loadUserLinkByUsername(
            userLinkService.oidcProvider,
            oidcUser.name
        )?.user ?: userLinkService.registerNewOidcUser(oidcUser, role)



        return ElaasticOidcUser(oidcUser, user)
    }
}