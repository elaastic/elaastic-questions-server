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

package org.elaastic.auth

import org.elaastic.auth.oauth.ElaasticOidcUser
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.cas.authentication.CasAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Handle logout on Elaastic
 * This handler delegates to oidcClientInitiatedLogoutSuccessHandler of OIDC and
 * to elaasticUrlLogoutSuccessHandler for FormLogin and CAS login
 * @author John Tranier
 */
class ElaasticLogoutSuccessHandler(
    private val elaasticUrlLogoutSuccessHandler: LogoutSuccessHandler,
    private val oidcClientInitiatedLogoutSuccessHandler: OidcClientInitiatedLogoutSuccessHandler,
) : LogoutSuccessHandler {

    override fun onLogoutSuccess(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication?
    ) {
        if (authentication?.principal is ElaasticOidcUser) {
            oidcClientInitiatedLogoutSuccessHandler.onLogoutSuccess(request, response, authentication)
        } else if (authentication is UsernamePasswordAuthenticationToken || authentication is CasAuthenticationToken) {
            elaasticUrlLogoutSuccessHandler.onLogoutSuccess(request, response, authentication)
        } else {
            throw UnsupportedOperationException(
                "This authentication implementation is not supported: ${authentication?.javaClass?.name}"
            )
        }
    }
}