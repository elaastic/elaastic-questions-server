package org.elaastic.auth.cas

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
        return userLinkService.loadUserByUsername(casKey, username) ?:
        userLinkService.registerNewCasUser(casKey, casProvider, token.assertion.principal)
    }
}