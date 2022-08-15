package org.elaastic.questions.security

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

        }
    }
}