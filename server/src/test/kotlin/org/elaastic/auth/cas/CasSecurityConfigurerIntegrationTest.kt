/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019-2026. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
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

import org.elaastic.security.CasSecurityConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.hamcrest.MatcherAssert.*
import org.junit.jupiter.api.Test
import org.hamcrest.CoreMatchers.*


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class CasSecurityConfigurerIntegrationTest(
    @Autowired val casSecurityConfigurer: CasSecurityConfig.CasSecurityConfigurer,
) {

    @Test
    fun testCasSecurityConfig() {
        with(casSecurityConfigurer) {
            assertThat(casSecurityConfigurer.casInfoList.size, equalTo(2))
            assertThat(
                getServiceCasLoginUrl("ENT_1"),
                equalTo("http://localhost:8080/login/cas/ENT_1")
            )

            assertThat(
                getServiceCasLoginUrl("ENT_2"),
                equalTo("http://localhost:8080/login/cas/ENT_2")
            )

            assertThat(
                getCasAuthenticationProviderBean("ENT_1"),
                notNullValue()
            )
            assertThat(
                getCasAuthenticationProviderBean("ENT_2"),
                notNullValue()
            )

            assertThat(
                getCasAuthenticationEntryPointBean("ENT_1").loginUrl,
                equalTo("https://localhost:8443/cas/login")
            )

            assertThat(
                getCasAuthenticationEntryPointBean("ENT_2").loginUrl,
                equalTo("https://localhost:8444/cas/login")
            )

            assertThat(casInfoList[0].casKey, equalTo("ENT_1"))
            assertThat(casInfoList[0].label, equalTo("ENT_1"))
            assertThat(casInfoList[0].logoSrc, equalTo("/images/cas/ecollege.haute-garonne.long.png"))
            assertThat(casInfoList[0].casProvider, equalTo(SupportedCasProvider.Kosmos.name))
            assertThat(casInfoList[1].casProvider, equalTo(SupportedCasProvider.Edifice.name))


        }
    }
}