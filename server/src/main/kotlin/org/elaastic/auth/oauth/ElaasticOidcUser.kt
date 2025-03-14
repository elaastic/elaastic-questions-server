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

import org.elaastic.user.PrincipalUserResolver
import org.elaastic.user.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.oidc.user.OidcUser

/**
 * Represents an OidcUser bound to an ElaasticUser
 * @author John Tranier
 */
class ElaasticOidcUser(oidcUser: OidcUser, override val elaasticUser: User) : OidcUser by oidcUser, PrincipalUserResolver {

    /**
     * Note JT: This is required by the <span sec:authentication="principal.fullname"> used in Layout
     * Perhaps the template could be use another way to get the fullname...
     */
    fun getFullname() = elaasticUser.getFullname()

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return elaasticUser.authorities
    }
}