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
                language: String = "fr",
                checkEmailAccount: Boolean = false
                ): User? {

        require(user.roles?.isNotEmpty())

        user.password = passwordEncoder.encode(user.password)

        // TODO ActivationKey
        // TODO Settings

        return userRepository.save(user)
    }
}