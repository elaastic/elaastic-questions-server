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
