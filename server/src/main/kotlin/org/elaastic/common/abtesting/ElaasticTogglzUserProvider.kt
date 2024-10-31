package org.elaastic.common.abtesting

import org.elaastic.questions.directory.User
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
        val principal = authentication.principal
        if (principal !is UserDetails) {
            return principal.toString()
        }
        return principal.username
    }

    private fun getActiveSince(authentication: Authentication) : LocalDate? {
        val principal = authentication.principal
        return if (principal !is User) {
            null
        } else {
            principal.activeSince
        }
    }
}