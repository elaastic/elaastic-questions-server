package org.elaastic.questions.directory.controller

import org.elaastic.questions.directory.RoleService
import org.elaastic.questions.directory.User
import org.elaastic.questions.directory.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import java.util.logging.Logger

@RestController
class UserAccountApiController(

        @Autowired val roleService: RoleService,
        @Autowired val userService: UserService,

        @Value("\${elaastic.auth.check_user_email:true}")
        val checkEmail: Boolean,

        @Autowired val messageSource: MessageSource
) {

    val logger = Logger.getLogger(UserAccountApiController::class.java.name)

    @PostMapping("/api/users")
    fun ajaxDoSubscribe(userData: UserData, locale: Locale): SubscriptionResponse {
        val user = User(
                username = userData.username,
                plainTextPassword = userData.password,
                email = userData.email,
                firstName = userData.firstName,
                lastName = userData.lastName
        ).addRole(
                roleService.roleForName(name = userData.role, attachedToCurrentTransaction = true)
        )
        lateinit var subscriptionResponse: SubscriptionResponse
        try {
            userService.addUser(
                    user = user,
                    language = userData.language,
                    checkEmailAccount = checkEmail
            )
            subscriptionResponse = SubscriptionResponse()
        } catch (e: DataIntegrityViolationException) {
            logger.severe(e.toString())
            val message = messageSource.getMessage("user.normalizedUsername.unique", emptyArray(),locale)
            subscriptionResponse = SubscriptionResponse(
                    success = false,
                    errorList = listOf(message)
            )
        }
        return subscriptionResponse
    }

    data class UserData(
            val firstName: String,
            val lastName: String,
            val email: String,
            val role: String,
            val username: String,
            val password: String,
            val password2: String,
            val language: String
    )

    data class SubscriptionResponse(
            val success: Boolean = true,
            val errorList: List<String> = listOf()
    )
}
