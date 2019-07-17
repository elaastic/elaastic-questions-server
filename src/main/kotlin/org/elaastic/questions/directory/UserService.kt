package org.elaastic.questions.directory

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

/**
 * @author John Tranier
 */
@Service
class UserService (
        @Autowired val userRepository: UserRepository,
        @Autowired val passwordEncoder: PasswordEncoder
) {

    fun findByUsername(username: String) : User? {
        return userRepository.findByUsername(username)
    }

    fun addUser(user: User,
                // TODO check if necessary mainRole: Role,
                language: String = "fr",
                checkEmailAccount: Boolean = false
                ): User? {

        user.password = passwordEncoder.encode(user.password)

        // TODO ActivationKey
        // TODO Settings

        return userRepository.save(user)
    }
}