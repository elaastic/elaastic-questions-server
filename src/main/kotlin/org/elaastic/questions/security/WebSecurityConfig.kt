package org.elaastic.questions.security

import org.elaastic.questions.directory.ElaasticUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * @author John Tranier
 */
@Configuration
@EnableWebSecurity
class WebSecurityConfig(
        @Autowired val elaasticUserDetailsService: ElaasticUserDetailsService
) : WebSecurityConfigurerAdapter() {

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(authenticationProvider())
    }

    override fun configure(http: HttpSecurity?) {
        http
                ?.authorizeRequests()
                    ?.antMatchers("/images/**", "/css/**", "/js/**", "/", "/demo")?.permitAll()
                    ?.anyRequest()?.authenticated()
                    ?.and()
                ?.formLogin()
                    ?.and()
                ?.httpBasic()
    }

    @Bean
    fun authenticationProvider(): DaoAuthenticationProvider {
        DaoAuthenticationProvider().let {
            it.setUserDetailsService(elaasticUserDetailsService)
            it.setPasswordEncoder(encoder())
            return it
        }
    }

    @Bean
    fun encoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

}
