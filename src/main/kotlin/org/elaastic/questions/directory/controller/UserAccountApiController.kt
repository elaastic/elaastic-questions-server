package org.elaastic.questions.directory.controller

import org.elaastic.questions.directory.RoleService
import org.elaastic.questions.directory.UserService
import org.elaastic.questions.directory.controller.command.UserData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import java.util.logging.Logger
import javax.validation.Valid
import javax.validation.ValidationException

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
    fun ajaxDoSubscribe(
            @Valid userData: UserData,
            result: BindingResult,
            locale: Locale
    ): SubscriptionResponse {
        if (!result.hasErrors()) {
            val user = userData.populateNewUser(roleService)
            try {
                userService.addUser(
                        user = user,
                        language = userData.language,
                        checkEmailAccount = checkEmail
                )
            } catch (e: DataIntegrityViolationException) {
                userData.catchDataIntegrityViolationException(e, result)
            }
        }
        return if (result.hasErrors()) {
            result.allErrors.map {
                it.codes?.let { codes ->
                    messageSource.getMessage(codes[0], emptyArray(), locale)
                }
            }.let {
                SubscriptionResponse(
                        success = false,
                        errorList = it as List<String>
                )
            }
        } else {
            SubscriptionResponse()
        }
    }


    data class SubscriptionResponse(
            val success: Boolean = true,
            val errorList: List<String> = listOf()
    )
}
