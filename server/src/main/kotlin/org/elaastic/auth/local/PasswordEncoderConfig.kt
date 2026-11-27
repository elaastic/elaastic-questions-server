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

package org.elaastic.auth.local

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.DelegatingPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class PasswordEncoderConfig {
    companion object {
        const val ENCODER_ID_BCRYPT = "bcrypt"
        const val ENCODER_ID_ARGON_5_8 = "argon2@springsec-5.8"

        const val CURRENT_PASSWORD_ENCODER = ENCODER_ID_ARGON_5_8
    }

    @Bean
    fun encoder(): PasswordEncoder {
        return DelegatingPasswordEncoder(
            CURRENT_PASSWORD_ENCODER,
            mapOf(
                ENCODER_ID_BCRYPT to BCryptPasswordEncoder(),
                ENCODER_ID_ARGON_5_8 to Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8(),
            )
        )
    }
}
