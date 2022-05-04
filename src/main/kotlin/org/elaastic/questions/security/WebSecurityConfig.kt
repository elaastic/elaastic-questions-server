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

import org.elaastic.questions.directory.Role
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.AnyRequestMatcher

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfig(
    @Autowired val userDetailsService: UserDetailsService,
    @Autowired val encoder: PasswordEncoder,
) : WebSecurityConfigurerAdapter() {

    @Autowired var casSecurityConfigurer: CasSecurityConfig.CasSecurityConfigurer? = null

    override fun configure(auth: AuthenticationManagerBuilder) {
        casSecurityConfigurer?.getCasAuthenticationProviderBeanList()?.forEach {
            auth.authenticationProvider(it)
        }

        auth.authenticationProvider(authenticationProvider())
    }

    @Throws(Exception::class)
    override fun configure(web: WebSecurity) {
        web.ignoring().antMatchers(HttpMethod.POST, "/launch", "/elaastic-questions/launch")
    }

    override fun configure(http: HttpSecurity) {
        http {

            logout {
                logoutRequestMatcher = AntPathRequestMatcher("/logout")
                logoutSuccessUrl = "/"
                clearAuthentication = true
                deleteCookies("JSESSIONID")
                invalidateHttpSession = true
            }

            authorizeRequests {
                authorize("/images/**", permitAll)
                authorize("/css/**", permitAll)
                authorize("/js/**", permitAll)
                authorize("/semantic/**", permitAll)
                authorize("/", permitAll)
                authorize("/demo", permitAll)
                authorize("/ckeditor/**", permitAll)
                authorize("/register", permitAll)
                authorize("/api/users", permitAll)
                authorize("/login", permitAll)

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
                authorize(anyRequest, authenticated)
            }

            formLogin {
                loginPage = "/login"
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
                        AnyRequestMatcher.INSTANCE to LoginUrlAuthenticationEntryPoint("/login")
                    )

                )
            }

        }
    }

    @Bean
    fun authenticationProvider(): DaoAuthenticationProvider {
        DaoAuthenticationProvider().let {
            it.setUserDetailsService(userDetailsService)
            it.setPasswordEncoder(encoder)
            return it
        }
    }

    @Bean
    @Throws(Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }
}
