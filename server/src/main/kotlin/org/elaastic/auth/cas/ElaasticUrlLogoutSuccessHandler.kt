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

package org.elaastic.auth.cas

import org.elaastic.user.PrincipalUserResolver
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

typealias CasKey = String
typealias Url = String

/**
 * This LogoutSuccessHandler manage logouts from the elaastic formLogin and the configured CAS servers as well
 * If the session is not bound to a CAS session, it will redirect to formLogoutSuccessUrl
 * If the session is bound to a CAS session, it will redirect to the CAS server logout URL
 */
class ElaasticUrlLogoutSuccessHandler(
    formLogoutSuccessUrl: String,
    private val casKeyToServerUrl: Map<CasKey, Url>,
    private val casLogoutSuccessUrl: String,
) : SimpleUrlLogoutSuccessHandler(), LogoutSuccessHandler {

    init {
        this.defaultTargetUrl = formLogoutSuccessUrl
    }


    override fun determineTargetUrl(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication?
    ): String {
        val user = (authentication?.principal as PrincipalUserResolver?)?.elaasticUser

        return if (user?.casKey != null) {
            (casKeyToServerUrl[user.casKey]
                ?: throw IllegalStateException("No logout URL configured for the CAS server [${user.casKey}]")) +
                    casLogoutSuccessUrl
        } else super.determineTargetUrl(request, response, authentication)
    }
}