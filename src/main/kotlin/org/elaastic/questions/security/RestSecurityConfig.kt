package org.elaastic.questions.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@ConditionalOnProperty(name = ["rest.api.enabled"], havingValue = "true")
@Order(1)
class RestSecurityConfig(
    @Value("\${rest.user.name}") private val restUserName: String,
    @Value("\${rest.user.password}") private val restUserPassword: String,
    @Value("\${rest.api.allowed_url}") private val restAllowedUrl: String
) {

    companion object {
        const val ROLE_REST_CLIENT = "rest-client"
    }

    @Bean
    fun restFilterChain(http: HttpSecurity): SecurityFilterChain {
        (http.securityMatcher("/api/practice/**")) {

            authorizeHttpRequests {
                authorize("/api/practice/**", hasAuthority(ROLE_REST_CLIENT))
            }

            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }

            httpBasic { }

            cors {
                val config = CorsConfiguration().apply {
                    allowedOrigins = listOf(restAllowedUrl)
                    allowedMethods = listOf("GET")
                    allowedHeaders = listOf("*")
                    allowCredentials = true
                }
                UrlBasedCorsConfigurationSource().apply {
                    registerCorsConfiguration("/api/practice/**", config)
                }
            }

            csrf { disable() }

            exceptionHandling {}
        }

        http.authenticationManager(restAuthenticationManager())

        return http.build()
    }

    fun restAuthenticationManager() = ProviderManager(restAuthenticationProvider())

    fun restAuthenticationProvider(): AuthenticationProvider =
        object : AuthenticationProvider {
            override fun authenticate(authentication: Authentication): Authentication {
                val username = authentication.principal.toString()
                val password = authentication.credentials.toString()

                if (username == restUserName && password == restUserPassword) {
                    val authorities: List<GrantedAuthority> = listOf(SimpleGrantedAuthority(ROLE_REST_CLIENT))
                    val userDetails: UserDetails =
                        org.springframework.security.core.userdetails.User(username, password, authorities)
                    return UsernamePasswordAuthenticationToken(userDetails, password, authorities)
                }

                throw BadCredentialsException("Invalid username or password")
            }

            override fun supports(authentication: Class<*>?) =
                authentication == UsernamePasswordAuthenticationToken::class.java
        }

}