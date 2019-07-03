package org.elaastic.questions.security

import org.elaastic.questions.directory.Role
import org.elaastic.questions.directory.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/**
 * @author John Tranier
 */
class ElaasticPrincipal(val user: User): UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return user.roles.map { it -> SimpleGrantedAuthority(it.name) }.toMutableList()
    }

    override fun isEnabled(): Boolean {
        return user.enabled
    }

    override fun getUsername(): String {
        return user.username
    }

    override fun isCredentialsNonExpired(): Boolean {
        return !user.passwordExpired
    }

    override fun getPassword(): String? {
        return user.password
    }

    override fun isAccountNonExpired(): Boolean {
        return !user.accountExpired
    }

    override fun isAccountNonLocked(): Boolean {
        return !user.accountLocked
    }
}