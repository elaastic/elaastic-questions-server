package org.elaastic.questions.security

import org.elaastic.questions.directory.Role
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.WebSecurity






@Configuration
@EnableWebSecurity
class WebSecurityConfig(
        @Autowired val userDetailsService: UserDetailsService
) : WebSecurityConfigurerAdapter() {

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(authenticationProvider())
    }

    @Throws(Exception::class)
    override fun configure(web: WebSecurity) {
        web.ignoring().antMatchers(HttpMethod.POST,"/launch")
    }

    override fun configure(http: HttpSecurity?) {

        http
                ?.logout()
                    ?.logoutRequestMatcher(AntPathRequestMatcher("/logout"))
                    ?.logoutSuccessUrl("/")
                    ?.clearAuthentication(true)
                    ?.deleteCookies("JSESSIONID")
                    ?.invalidateHttpSession(true)
                ?.and()
                ?.authorizeRequests()
                    ?.antMatchers(
                            "/images/**",
                            "/css/**",
                            "/js/**",
                            "/semantic/**",
                            "/",
                            "/demo",
                            "/ckeditor/**"
                    )?.permitAll()
                    ?.antMatchers(
                            "/register",
                            "/api/users",
                            "/login",
                            "/userAccount/beginPasswordReset",
                            "/userAccount/resetPassword",
                            "/userAccount/processPasswordResetRequest",
                            "/userAccount/confirmPasswordReset",
                            "/userAccount/processResetPassword",
                            "/userAccount/activate",
                            "/terms"
                            )?.permitAll()
                    ?.antMatchers("/ltiConsumer/**")?.hasAuthority(Role.RoleId.ADMIN.roleName)
                    ?.anyRequest()?.authenticated()
                    ?.and()
                ?.formLogin()
                    ?.loginPage("/login")?.defaultSuccessUrl("/home")
                    ?.permitAll()
    }

    @Bean
    fun authenticationProvider(): DaoAuthenticationProvider {
        DaoAuthenticationProvider().let {
            it.setUserDetailsService(userDetailsService)
            it.setPasswordEncoder(encoder())
            return it
        }
    }

    @Bean
    fun encoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    @Throws(Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

}
