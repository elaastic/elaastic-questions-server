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

import org.springframework.context.MessageSource
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.web.util.UriComponentsBuilder
import java.util.Locale
import javax.servlet.http.HttpServletRequest

const val USER_REQUEST_ATTRIBUTE = "userRequest"
const val ERROR_ROLE_URL = "/error/oidc_role"

@Controller
class RoleExceptionController(private val messageSource: MessageSource) {
    @GetMapping(ERROR_ROLE_URL)
    fun handleRoleException(model: Model, locale: Locale, request: HttpServletRequest): String {
        val userRequest = request.session.getAttribute(USER_REQUEST_ATTRIBUTE) as? OidcUserRequest
        request.session.removeAttribute(USER_REQUEST_ATTRIBUTE)
        model["errorMessage"] = messageSource.getMessage("oidc.role_error", null, locale)
        // Allow ending the provider session after a rejected login without authenticating an Elaastic user.
        val endSessionEndpoint = userRequest?.clientRegistration?.providerDetails
            ?.configurationMetadata?.get("end_session_endpoint") as? String
        model["logoutUrl"] = if (endSessionEndpoint != null) {
            UriComponentsBuilder.fromUriString(endSessionEndpoint)
                .queryParam("id_token_hint", userRequest.idToken.tokenValue)
                .build().encode().toUriString()
        } else request.contextPath + "/login"
        return "error/oidc_role"
    }
}
