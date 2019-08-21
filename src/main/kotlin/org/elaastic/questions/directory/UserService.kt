package org.elaastic.questions.directory

import org.apache.commons.lang3.time.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Example
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import java.util.logging.Logger
import javax.persistence.EntityManager
import javax.transaction.Transactional


@Service
class UserService(
        @Autowired val userRepository: UserRepository,
        @Autowired val passwordEncoder: PasswordEncoder,
        @Autowired val settingsRepository: SettingsRepository,
        @Autowired val unsubscribeKeyRepository: UnsubscribeKeyRepository,
        @Autowired val activationKeyRepository: ActivationKeyRepository,
        @Autowired val passwordResetKeyRepository: PasswordResetKeyRepository
) {

    val logger = Logger.getLogger(UserService::class.java.name)

    /**
     * Find user by id
     * @param id the id
     * @return the found user or null
     */
    fun findById(id: Long): User? {
        userRepository.findById(id).let {
            return if (it.isPresent) {
                it.get()
            } else {
                null
            }
        }
    }


    /**
     *  Find user by username
     *  @param username the username provided as input
     *  @return the found user or null otherwise
     */
    fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }

    /**
     *  Find user by email
     *  @param email the email provided as input
     *  @return the found user or null otherwise
     */
    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
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

    @Transactional
    fun saveUser(authUser: User, user: User):User {
        if (authUser != user) {
            logger.severe("Trying illegal access on user ${user.id} from ${authUser.id}")
            throw AccessDeniedException("You are not authorized to access to this user")
        }
        if (user.plainTextPassword != null) {
            user.password = passwordEncoder.encode(user.plainTextPassword)
        }
        userRepository.saveAndFlush(user).let {
            return it
        }
    }

    /**
     * Change the password of a user
     * @param user the processed user
     * @return the user with its new password
     */
    fun changePasswordForUser(user: User, newPlainTextPassword: String): User {
        user.plainTextPassword = newPlainTextPassword // required to get validation
        user.password = passwordEncoder.encode(newPlainTextPassword)
        userRepository.saveAndFlush(user).let {
            return it
        }
    }

    /**
     * Initialize settings for a new user
     * @param user the user
     * @return the settings object
     */
    fun initializeSettingsForUser(user: User, language: String): Settings {
        Settings(user = user, language = language).let {
            user.settings = it
            settingsRepository.save(it).let {
                return it
            }
        }
    }

    /**
     * Initialize unsubscribe key for a new user
     * @param user the user
     * @return the unsubscribe key object
     */
    fun initializeUnsubscribeKeyForUser(user: User): UnsubscribeKey {
        UnsubscribeKey(
                user = user,
                unsubscribeKey = UUID.randomUUID().toString()
        ).let {
            unsubscribeKeyRepository.save(it)
        }.let {
            user.unsubscribeKey = it
            return it
        }
    }

    /**
     * Initialize activation key for a new user
     * @param user the user
     * @return the activation key object
     */
    fun initializeActivationKeyForUser(user: User): ActivationKey {
        ActivationKey(
                user = user,
                activationKey = UUID.randomUUID().toString()
        ).let {
            activationKeyRepository.save(it)
        }.let {
            user.activationKey = it
            return it
        }
    }

    /**
     * Enable user with activation key
     * @param activationKey the string value of the activation key
     * @return the enabled user or null if no activation key is found
     */
    fun enableUserWithActivationKey(activationKey: String): User? {
        activationKeyRepository.findByActivationKey(activationKey).let {
            when (it) {
                null -> return null
                else -> {
                    val user = it.user
                    user.enabled = true
                    userRepository.save(user)
                    activationKeyRepository.delete(it)
                    return it.user
                }
            }
        }


    }

    /**
     * Generate password reset key for a  user
     * @param user the processed user
     * @param lifetime lifetime of a password key in hour, default set to 1
     * @return the password reset key object user
     */
    fun generatePasswordResetKeyForUser(user: User, lifetime: Int = 1): PasswordResetKey {
        var passwordResetKey = passwordResetKeyRepository.findByUser(user)
        when (passwordResetKey) {
            null -> {
                PasswordResetKey(
                        passwordResetKey = UUID.randomUUID().toString(),
                        user = user
                )
            }
            else -> {
                if (passwordResetKey.dateCreated < DateUtils.addHours(Date(), -lifetime)) {
                    passwordResetKey.passwordResetKey = UUID.randomUUID().toString()
                    passwordResetKey.dateCreated = Date()
                }
                passwordResetKey.passwordResetEmailSent = false
                passwordResetKey
            }
        }.let {
            passwordResetKeyRepository.saveAndFlush(it)
        }.let {
            return it
        }
    }


    /**
     * Find user by password reset key
     * @param passwordResetKeyValue the string value of the password reset key
     * @return the found user or null otherwise
     */
    fun findByPasswordResetKeyValue(passwordResetKeyValue: String): User? {
        userRepository.findByPasswordResetKeyValue(passwordResetKeyValue).let {
            return it
        }
    }

    /**
     * Remove old activation keys and corresponding users who didn't activate their
     * accounts
     * @param lifetime the lifetime in hours of activation keys, default set to 3
     */
    fun removeOldActivationKeys(lifetime: Int = 3) {
        activationKeyRepository.findAllByDateCreatedLessThan(DateUtils.addHours(Date(), -lifetime)).let {
            activationKeyRepository.deleteAll(it)
            logger.info("${it.size} activation keys deleted")
            it
        }.map {
            it.user
        }.filter { user ->
            !user.enabled
        }.let {
            logger.info("${it.size} user(s) to delete")
            it
        }.forEach { user ->
            settingsRepository.findByUser(user).let { settings ->
                settingsRepository.delete(settings)
            }
            unsubscribeKeyRepository.findByUser(user).let { unsubscribeKey ->
                unsubscribeKeyRepository.delete(unsubscribeKey)
            }
            userRepository.delete(user)
        }
    }

    /**
     * Remove password reset keys older than lifetime hours, default to 1
     */
    fun removeOldPasswordResetKeys(lifetime: Int = 1) {
        passwordResetKeyRepository.findAllByDateCreatedLessThan(DateUtils.addHours(Date(), -lifetime)).let {
            passwordResetKeyRepository.deleteAll(it)
            logger.info("${it.size} password reset keys deleted")
        }
    }
}

