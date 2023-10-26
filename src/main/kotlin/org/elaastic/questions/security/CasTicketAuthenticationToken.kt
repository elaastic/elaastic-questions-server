package org.elaastic.questions.security

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.util.Assert

class CasTicketAuthenticationToken(val casKey: String, principal: Any, credentials: Any) :
    AbstractAuthenticationToken(null) {

    private var _principal: Any = principal
    private var _credentials: Any? = credentials


    init {
        isAuthenticated = false
    }

    init {
        super.setAuthenticated(false)
    }

    override fun getCredentials() = _credentials

    override fun getPrincipal() = _principal

    @Throws(IllegalArgumentException::class)
    override fun setAuthenticated(isAuthenticated: Boolean) {
        Assert.isTrue(
            !isAuthenticated,
            "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead"
        )
        super.setAuthenticated(false)
    }

    override fun eraseCredentials() {
        super.eraseCredentials()
        _credentials = null
    }
}