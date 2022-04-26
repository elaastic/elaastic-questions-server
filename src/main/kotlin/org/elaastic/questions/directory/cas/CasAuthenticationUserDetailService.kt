package org.elaastic.questions.directory.cas

import org.jasig.cas.client.authentication.AttributePrincipal
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService
import org.springframework.security.core.userdetails.UserDetails

class CasAuthenticationUserDetailService(
    private val casUserDetailService: CasUserDetailService,
    private val entId: String,
) : AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {


    override fun loadUserDetails(token: CasAssertionAuthenticationToken): UserDetails {
        val username: String = token.name
        return casUserDetailService.loadUserByUsername(entId, username) ?:
        casUserDetailService.registerNewCasUser(entId, token.assertion.principal)
    }
}