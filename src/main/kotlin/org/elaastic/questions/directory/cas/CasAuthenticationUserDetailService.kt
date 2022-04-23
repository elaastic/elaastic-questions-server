package org.elaastic.questions.directory.cas

import org.jasig.cas.client.authentication.AttributePrincipal
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class CasAuthenticationUserDetailService(
    @Autowired val casUserDetailService: CasUserDetailService
) : AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {


    override fun loadUserDetails(token: CasAssertionAuthenticationToken): UserDetails {
        val principal: AttributePrincipal = token.assertion.principal

        val username: String = token.name
        return casUserDetailService.loadUserByUsername(username) ?:
        casUserDetailService.registerNewCasUser(username, token.assertion.principal)
    }
}