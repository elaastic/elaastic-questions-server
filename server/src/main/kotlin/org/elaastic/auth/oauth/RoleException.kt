package org.elaastic.auth.oauth

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.OAuth2Error

class RoleException : OAuth2AuthenticationException {

    val userRequest: OidcUserRequest?

    constructor(oidcUser: OidcUserRequest, message: String, cause: Throwable? = null) :
            super(OAuth2Error("role_error", message, null), cause) {
        this.userRequest = oidcUser
    }

    constructor(message: String) : super(OAuth2Error("role_error", message, null)) {
        this.userRequest = null
    }
}