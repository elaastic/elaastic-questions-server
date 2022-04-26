package org.elaastic.questions.directory.cas

import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService
import org.springframework.security.core.userdetails.UserDetails

class CasAuthenticationUserDetailService(
    private val casUserDetailService: CasUserDetailService,
    private val casKey: String,
) : AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {


    override fun loadUserDetails(token: CasAssertionAuthenticationToken): UserDetails {
        val username: String = token.name
        return casUserDetailService.loadUserByUsername(casKey, username) ?:
        casUserDetailService.registerNewCasUser(casKey, token.assertion.principal)
    }
}