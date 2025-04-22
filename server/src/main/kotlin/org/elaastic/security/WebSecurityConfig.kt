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

package org.elaastic.security

import org.elaastic.auth.ElaasticLogoutSuccessHandler
import org.elaastic.auth.cas.ElaasticUrlLogoutSuccessHandler
import org.elaastic.auth.oauth.ElaasticOidcUserService
import org.elaastic.auth.oauth.OidcHintFilter
import org.elaastic.user.Role
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.AnyRequestMatcher


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Order(3)
class WebSecurityConfig(
    @Autowired val userDetailsService: UserDetailsService,
    @Autowired val encoder: PasswordEncoder,
    @Autowired val elaasticOidcUserService: ElaasticOidcUserService,
    @Autowired val clientRegistrationRepository: ClientRegistrationRepository,
    @Value("\${elaastic.questions.url}") val elaasticUrl: String,
    @Value("\${elaastic.openid.enabled:false}") val elaasticOidcEnabled: Boolean,
) {

    companion object {
        const val LOGIN_URL = "/login"
    }

    @Autowired
    var casSecurityConfigurer: CasSecurityConfig.CasSecurityConfigurer? = null

    @Bean
    fun webAuthenticationManager(): AuthenticationManager {
        val providers = mutableListOf<AuthenticationProvider>()
        providers.addAll(casSecurityConfigurer?.getCasAuthenticationProviderBeanList() ?: listOf())
        providers.add(daoAuthenticationProvider())

        return ProviderManager(providers)
    }

    @Bean
    fun webSecurityCustomize() =
        WebSecurityCustomizer { web ->
            web.ignoring().requestMatchers(HttpMethod.POST, "/launch", "/elaastic-questions/launch")

        }

    @Bean
    @Order(0)
    fun resourceFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .securityMatcher("/images/**", "/css/**", "/js/**", "/semantic/**", "/ckeditor/**")
            .authorizeHttpRequests { authorize -> authorize.anyRequest().permitAll() }
            .requestCache().disable()
            .securityContext().disable()
            .sessionManagement().disable()

        return http.build()
    }

    @Bean
    fun webFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {

            if (elaasticOidcEnabled) {
                oauth2Login {
                    Customizer.withDefaults<OAuth2LoginConfigurer<HttpSecurity>>()
                    userInfoEndpoint {
                        oidcUserService = elaasticOidcUserService
                    }
                }
                http.addFilterBefore(
                    OidcHintFilter(),
                    UsernamePasswordAuthenticationFilter::class.java
                )
            }

            val elaasticUrlLogoutSuccessHandler = ElaasticUrlLogoutSuccessHandler(
                "/",
                casSecurityConfigurer?.casKeyToServerUrl ?: mapOf(),
                "/logout?service=${elaasticUrl}"
            )

            val oidcClientInitiatedLogoutSuccessHandler =
                OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository)
                    .also {
                        it.setPostLogoutRedirectUri(elaasticUrl)
                    }

            logout {
                logoutRequestMatcher = AntPathRequestMatcher("/logout")
                logoutSuccessHandler = ElaasticLogoutSuccessHandler(
                    elaasticUrlLogoutSuccessHandler,
                    oidcClientInitiatedLogoutSuccessHandler,
                )
                clearAuthentication = true
                deleteCookies("JSESSIONID")
                invalidateHttpSession = true

            }

            authorizeRequests {
                authorize("/", permitAll)
                authorize("/demo", permitAll)
                authorize("/ui/**", permitAll)
                authorize("/register", permitAll)
                authorize("/api/users", permitAll)
                authorize(LOGIN_URL, permitAll)

                // Allow access to this URL on which the CAS filter are applied
                // A CAS filter will validate the provided ticket (URL argument) against the configured CAS server
                // Each CAS filter monitor its own URL in the form of /login/cas/<casKey>
                authorize("/login/cas/*", permitAll)

                authorize("/player/start-anonymous-session", permitAll)
                authorize("/userAccount/beginPasswordReset", permitAll)
                authorize("/userAccount/resetPassword", permitAll)
                authorize("/userAccount/processPasswordResetRequest", permitAll)
                authorize("/userAccount/confirmPasswordReset", permitAll)
                authorize("/userAccount/processResetPassword", permitAll)
                authorize("/userAccount/activate", permitAll)
                authorize("/terms", permitAll)
                authorize("/launch/consent", permitAll)
                authorize("/player/register", permitAll)
                authorize("/ltiConsumer/**", hasAuthority(Role.RoleId.ADMIN.roleName))
                authorize("/chatgpt/prompt/**", hasAuthority(Role.RoleId.ADMIN.roleName))
                authorize(anyRequest, authenticated)
            }

            formLogin {
                loginPage = LOGIN_URL
                defaultSuccessUrl("/home", false)
            }

            // ExceptionHandling define the behavior when a Security Exception occurred (typically when the user is not
            // authenticated on a secured URL)
            exceptionHandling {
                authenticationEntryPoint = DelegatingAuthenticationEntryPoint(
                    linkedMapOf(
                        // URL of the form /cas/<casKey>/** are secured by the CAS server identified by <casKey>
                        // If a request without authentication get a URL of the form /cas/<casKey>/** the corresponding
                        // CasAuthenticationEntryPoint will be triggered (resulting in a redirect on the corresponding
                        // CAS server)
                        *casSecurityConfigurer?.getCasAuthenticationEntryPoints() ?: arrayOf(),

                        // Any other secured URL is handled by the native elaastic authentication (the formLogin)
                        AnyRequestMatcher.INSTANCE to LoginUrlAuthenticationEntryPoint(LOGIN_URL)
                    )

                )
            }

            cors { }
            csrf { }
        }

        // To allow the use of iframe
        http
            .headers()
            .frameOptions().sameOrigin()

        return http.build()
    }


    @Bean
    fun daoAuthenticationProvider(): DaoAuthenticationProvider {
        DaoAuthenticationProvider().let {
            it.setUserDetailsService(userDetailsService)
            it.setPasswordEncoder(encoder)
            return it
        }
    }
}
