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

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import javax.servlet.http.HttpServletRequest

@Controller
class CasController {

    /**
     * Redirect the authenticated user to the `{remaining-path}` of the URL.
     *
     * URL of the form `/cas/{casKey}/{remaining-path}` are secured by the CAS server identified by `{casKey}`.
     *
     * When the user is properly authenticated, this action will perform a redirect to {remaining-path} (queryString if
     * any is preserved). When there is no authentication, the security config will handle to redirect the user on the
     * CAS server login page.
     */
    @GetMapping("/cas/{casKey}/**", "/elaastic-questions/cas/{casKey}/**")
    fun casRedirect(
        request: HttpServletRequest,
        @PathVariable casKey: String
    ) = request.requestURL.toString()
        .replace("/cas/$casKey", "", true)
        .let { url ->
            "redirect:$url${request.queryString?.let { "?$it" } ?: ""}"
        }
}