package org.elaastic.questions.directory

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional

/**
 * @author John Tranier
 */
@Service
class UserService (
        @Autowired val userRepository: UserRepository,
        @Autowired val passwordEncoder: PasswordEncoder,
        @Autowired val settingsRepository: SettingsRepository,
        @Autowired val unsubscribeKeyRepository: UnsubscribeKeyRepository,
        @Autowired val activationKeyRepository: ActivationKeyRepository
) {

    fun findByUsername(username: String) : User? {
        return userRepository.findByUsername(username)
    }

    /**
     * Save and initialize settings for a user newly created
     * @param user the user to process
     * @param language the preferred language of the user
     * @param checkEmailAccount flag to indicates if mail checking must be perform by the system
     * @return the saved user
     *
     */
    @Transactional
    fun addUser(user: User,
                language: String = "fr",
                checkEmailAccount: Boolean = false
                ): User {

        require(user.roles.isNotEmpty())

        with(user) {
            enabled = !checkEmailAccount
            password = passwordEncoder.encode(plainTextPassword)
            userRepository.save(this).let {
                initializeSettingsForUser(it, language)
                initializeUnsubscribeKeyForUser(it)
                if (checkEmailAccount) {
                    initializeActivationKeyForUser(it)
                }
                return it
            }
        }
    }

    /**
     * Initialize settings for a new user
     * @param user
     * @return the processed user
     */
    fun initializeSettingsForUser(user: User, language: String): Settings {
        Settings(user = user, language = language).let {
            user.settings = it
            settingsRepository.save(it)
            return it
        }
    }

    /**
     * Initialize unsubscribe key for a new user
     * @param user
     * @return the processed user
     */
    fun initializeUnsubscribeKeyForUser(user: User): UnsubscribeKey {
        UnsubscribeKey(
                user = user,
                unsubscribeKey = UUID.randomUUID().toString()
        ).let {
            unsubscribeKeyRepository.save(it)
            return it
        }
    }

    /**
     * Initialize activation key for a new user
     * @param user
     * @return the processed user
     */
    fun initializeActivationKeyForUser(user: User): ActivationKey {
        ActivationKey(
                user = user,
                activationKey = UUID.randomUUID().toString()
        ).let {
            activationKeyRepository.save(it)
            return it
        }
    }

}
