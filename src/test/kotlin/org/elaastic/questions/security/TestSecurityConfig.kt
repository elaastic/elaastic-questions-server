package org.elaastic.questions.security

import org.elaastic.questions.directory.Role
import org.elaastic.questions.directory.User
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.security.core.userdetails.UserDetailsService



@TestConfiguration
class TestSecurityConfig {

    @Bean("userDetailsService")
    @Primary
    fun userDetailsService(): UserDetailsService {
        val teacherRole = Role(Role.RoleId.TEACHER.name)


        val teacher = User(
                username = "teacher",
                email = "teacher@elaastic.org",
                firstName = "Franck",
                lastName = "Sil",
                plainTextPassword = "1234"
        ).addRole(teacherRole)

        val users = mapOf(
                "teacher" to teacher
        )

        return UserDetailsService { username: String? -> users[username] }

    }
}