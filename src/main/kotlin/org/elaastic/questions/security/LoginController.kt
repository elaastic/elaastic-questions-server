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

package org.elaastic.questions.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import java.nio.file.Paths
import javax.servlet.http.HttpServletRequest

@Controller
class LoginController(
    @Autowired val casSecurityConfigurer: CasSecurityConfig.CasSecurityConfigurer
) {

    @GetMapping("/login")
    fun displayLoginForm(model: Model, request: HttpServletRequest): String {

        model.addAttribute(
            "casInfoList",
            casSecurityConfigurer.casInfoList
        )
        model.addAttribute(
            "casUrlWithServiceMap",
            casSecurityConfigurer.casInfoList.map { it.casKey }.associateWith {
                // get the current casInfo and append the /login to the casUrl
                val serverUrl = casSecurityConfigurer.casKeyToServerUrl[it]
                val casLoginUrl = Paths.get(serverUrl, "login").toString()
                val serviceUrl = casSecurityConfigurer.getServiceCasLoginUrl(it)
                "$casLoginUrl?service=$serviceUrl"
            }
        )

        return "login"
    }
}
