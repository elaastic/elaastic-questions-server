package org.elaastic.questions.directory.controller


import org.elaastic.questions.directory.RoleService
import org.elaastic.questions.directory.User
import org.elaastic.questions.directory.UserService
import org.elaastic.questions.directory.controller.command.PasswordData
import org.elaastic.questions.directory.controller.command.UserData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.util.*
import java.util.logging.Logger
import javax.servlet.http.HttpServletResponse
import javax.validation.*


@Controller
class UserAccountController(
        @Value("\${elaastic.auth.check_user_email:true}")
        val checkEmail: Boolean,
        @Autowired val userService: UserService,
        @Autowired val roleService: RoleService,
        @Autowired val messageSource: MessageSource
) {

    val logger = Logger.getLogger(UserAccountController::javaClass.name)

    @GetMapping("/register")
    fun showSubscribeForm(model: Model): String {
        model.addAttribute("checkEmail", checkEmail)
        return "/userAccount/showSubscribeForm"
    }

    @GetMapping("/userAccount/edit")
    fun edit(authentication: Authentication, model: Model): String {
        val user: User = authentication.principal as User
        val userToUpdate = userService.get(user.id!!)!!
        model.addAttribute("userData", UserData(userToUpdate))
        model.addAttribute("user", userToUpdate)
        return "/userAccount/edit"
    }


    @PostMapping("/userAccount/update")
    fun update(authentication: Authentication,
               @Valid @ModelAttribute userData: UserData,
               result: BindingResult,
               model: Model,
               response: HttpServletResponse,
               redirectAttributes: RedirectAttributes,
               locale: Locale): String {
        val authUser: User = authentication.principal as User
        if (!result.hasErrors()) {
            val updatedUser = userService.get(userData.id!!)!!
            userData.populateUser(updatedUser, roleService)
            try {
                userService.saveUser(authUser, updatedUser)
            } catch (e: DataIntegrityViolationException) {
                userData.catchDataIntegrityViolationException(e, result)
            }
        }
        return if (result.hasErrors()) {
            response.status = HttpStatus.BAD_REQUEST.value()
            model.addAttribute("user", authUser)
            model.addAttribute("userData", userData)
            "/userAccount/edit"
        } else {
            redirectAttributes.addFlashAttribute("messageType", "success")
            messageSource.getMessage("useraccount.update.success", emptyArray(), locale).let {
                redirectAttributes.addFlashAttribute("messageContent", it)
            }
            "redirect:/userAccount/edit"
        }
    }

    @GetMapping("/userAccount/editPassword")
    fun editPassword(authentication: Authentication, model: Model): String {
        val user: User = authentication.principal as User
        model.addAttribute("passwordData", PasswordData(user))
        model.addAttribute("user", user)
        return "/userAccount/editPassword"
    }

    @PostMapping("/userAccount/updatePassword")
    fun updatePassword(authentication: Authentication,
               @Valid @ModelAttribute passwordData: PasswordData,
               result: BindingResult,
               model: Model,
               response: HttpServletResponse,
               redirectAttributes: RedirectAttributes,
               locale: Locale): String {
        val authUser: User = authentication.principal as User
        if (!result.hasErrors()) {
            val updatedUser = userService.get(authUser, passwordData.id!!)!!
            try {
                userService.changePasswordForUserWithCurrentPasswordChecking(
                        updatedUser, passwordData.password!!, passwordData.password1!!)
            } catch (e: SecurityException) {
                passwordData.catchSecurityException(e, result)
            }
        }
        return if (result.hasErrors()) {
            response.status = HttpStatus.BAD_REQUEST.value()
            model.addAttribute("user", authUser)
            "/userAccount/editPassword"
        } else {
            redirectAttributes.addFlashAttribute("messageType", "success")
            messageSource.getMessage("useraccount.update.success", emptyArray(), locale).let {
                redirectAttributes.addFlashAttribute("messageContent", it)
            }
            "redirect:/userAccount/edit"
        }
    }

    @GetMapping("/userAccount/activate")
    fun doEnableUser(@RequestParam("actKey") activationKey: String, redirectAttributes: RedirectAttributes, locale: Locale): String {
        userService.enableUserWithActivationKey(activationKey).let {
            when (it) {
                null -> {
                    messageSource.getMessage("useraccount.activation.failure", emptyArray(), locale).let { mess ->
                        redirectAttributes.addFlashAttribute("message", mess)
                    }
                }
                else -> {
                    messageSource.getMessage("useraccount.activation.success", emptyArray(), locale).let { mess ->
                        redirectAttributes.addFlashAttribute("message", mess)
                    }
                }
            }
        }
        return "redirect:/login"
    }

}
