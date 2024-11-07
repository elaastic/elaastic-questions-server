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

import org.elaastic.user.Role
import org.elaastic.user.User
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder


@TestConfiguration
class TestSecurityConfig {

    @Bean("userDetailsService")
    @Primary
    fun userDetailsService(): UserDetailsService {
        val teacherRole = Role(Role.RoleId.TEACHER.name)
        val restClientRole = Role(RestSecurityConfig.ROLE_REST_CLIENT)


        val teacher = User(
            username = "teacher",
            email = "teacher@elaastic.org",
            firstName = "Franck",
            lastName = "Sil",
            plainTextPassword = "1234"
        ).addRole(teacherRole)

        val restClient = User(
            username = "rest-client",
            email = "contact@elaastic.com",
            firstName = "Rest",
            lastName = "Client",
            plainTextPassword = "1234"
        ).addRole(restClientRole)

        val users = mapOf(
            "teacher" to teacher,
            "restClient" to restClient,
        )

        return UserDetailsService { username: String? -> users[username] }
    }

    @Bean("passwordEncoder")
    fun passwordEncoder() = BCryptPasswordEncoder()
}
