/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019-2026. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
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

package org.elaastic.common.abtesting

import org.elaastic.user.PrincipalUserResolver
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.togglz.core.user.FeatureUser
import org.togglz.core.user.SimpleFeatureUser
import org.togglz.spring.security.SpringSecurityUserProvider
import java.time.LocalDate

class ElaasticTogglzUserProvider(private val featureAdminAuthority: String?) : SpringSecurityUserProvider(featureAdminAuthority) {

    val USER_ATTRIBUTE_ROLES = "roles"

    override fun getCurrentUser(): FeatureUser? {
        val context = SecurityContextHolder.getContext()
        val authentication = context.authentication ?: return null

        // null if no authentication data is available for the current thread

        // try to obtain the name of this user
        val name = getUserName(authentication)

        // check for the authority for feature admins
        val authorities = AuthorityUtils.authorityListToSet(authentication.authorities)
        val featureAdmin = isFeatureAdmin(authorities)

        val user = SimpleFeatureUser(name, featureAdmin)
        user.setAttribute(USER_ATTRIBUTE_ROLES, authorities)


        user.setAttribute("activeSince", getActiveSince(authentication))

        return user
    }

    private fun isFeatureAdmin(authorities: Set<String>): Boolean {
        return featureAdminAuthority != null && authorities.contains(featureAdminAuthority)
    }

    private fun getUserName(authentication: Authentication): String? {
        val principal = (authentication.principal as? PrincipalUserResolver)?.elaasticUser ?: authentication.principal
        if (principal !is UserDetails) {
            return principal.toString()
        }
        return principal.username
    }

    private fun getActiveSince(authentication: Authentication) : LocalDate? {
        return (authentication.principal as? PrincipalUserResolver)?.elaasticUser?.activeSince
    }
}
