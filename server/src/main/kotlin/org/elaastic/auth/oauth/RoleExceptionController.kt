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

import org.elaastic.user.User
import org.springframework.context.MessageSource
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.SessionAttribute
import java.util.*

const val USER_REQUEST_ATTRIBUTE = "userRequest"
const val ERROR_ROLE_URL = "/error/oidc_role"

@Controller
class RoleExceptionController(
    private val messageSource: MessageSource
) {

    @GetMapping(ERROR_ROLE_URL)
    fun handleRoleException(
        model: Model,
        @RequestParam("message", required = false) message: String?,
        locale: Locale,
        @SessionAttribute(name = USER_REQUEST_ATTRIBUTE, required = true) oidcUserRequest: OidcUserRequest
    ): String {
        /**
         * For the logout process to work, we need a valid authentication. So we create a fake authentication with the
         * OIDC user
         *
         * @see fakeAuthenticationFrom
         */
        SecurityContextHolder.setContext(SecurityContextImpl(fakeAuthenticationFrom(oidcUserRequest)))

        model["errorMessage"] = message ?: messageSource.getMessage("oidc.role_error", null, locale)
        return "error/oidc_role"
    }

    /**
     * Create a fake authentication from the OidcUserRequest
     *
     * As we only care for the [OidcUserRequest.idToken] and the [ClientRegistration.registrationId], we set all the
     * other information to default values.
     */
    private fun fakeAuthenticationFrom(
        oidcUserRequest: OidcUserRequest
    ): OAuth2AuthenticationToken {
        return OAuth2AuthenticationToken(
            ElaasticOidcUser(
                DefaultOidcUser(
                    emptyList(),
                    oidcUserRequest.idToken
                ),
                // We don't care about the information on the user
                User(
                    firstName = "FirstName",
                    lastName = "LastName",
                    username = "Username",
                    plainTextPassword = "Password",
                )
            ),
            emptyList(),
            oidcUserRequest.clientRegistration.registrationId
        )
    }
}