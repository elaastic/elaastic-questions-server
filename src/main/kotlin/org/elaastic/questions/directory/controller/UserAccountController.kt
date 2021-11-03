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

package org.elaastic.questions.directory.controller


import org.elaastic.questions.directory.RoleService
import org.elaastic.questions.directory.User
import org.elaastic.questions.directory.UserService
import org.elaastic.questions.directory.controller.command.PasswordData
import org.elaastic.questions.directory.controller.command.UserData
import org.elaastic.questions.onboarding.OnboardingChapter
import org.elaastic.questions.terms.TermsService
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
import java.lang.IllegalStateException
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
        @Autowired val termsService: TermsService,
        @Autowired val messageSource: MessageSource
) {

    val logger = Logger.getLogger(UserAccountController::javaClass.name)

    @GetMapping("/register")
    fun showSubscribeForm(model: Model): String {
        model.addAttribute("checkEmail", checkEmail)
        return "userAccount/showSubscribeForm"
    }

    @GetMapping("/userAccount/edit")
    fun edit(authentication: Authentication, model: Model): String {
        val user: User = authentication.principal as User
        if(user.isAnonymous()) throw IllegalStateException("Not allowed to anonymous user")

        val userToUpdate = userService.get(user.id!!)!!
        model.addAttribute("userData", UserData(userToUpdate, userHasGivenConsent = true))
        model.addAttribute("user", userToUpdate)
        return "userAccount/edit"
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
        if(authUser.isAnonymous()) throw IllegalStateException("Not allowed to anonymous user")
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
        if(user.isAnonymous()) throw IllegalStateException("Not allowed to anonymous user")
        model.addAttribute("passwordData", PasswordData(user))
        model.addAttribute("user", user)
        return "userAccount/editPassword"
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
        if(authUser.isAnonymous()) throw IllegalStateException("Not allowed to anonymous user")
        if (!result.hasErrors()) {
            val updatedUser = userService.get(authUser, passwordData.id!!)
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

    @ResponseBody
    @GetMapping("/userAccount/updateOnboardingChapter/{chapterToUpdate}")
    fun updateOnboardingChapter(authentication: Authentication, @PathVariable chapterToUpdate: String){
        val user: User = authentication.principal as User
        userService.updateOnboardingChapter(OnboardingChapter.from(chapterToUpdate), user)
    }

    @ResponseBody
    @GetMapping("/userAccount/getOnboardingChapter")
    fun getOnboardingChapter(authentication: Authentication): String? {
        val user: User = authentication.principal as User
        return userService.getOnboardingState(user.id).toString()
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


    @GetMapping("/userAccount/unsubscribe")
    fun unsubscribe(authentication: Authentication, model: Model, locale: Locale):String {
        val authUser: User = authentication.principal as User
        if(authUser.isAnonymous()) throw IllegalStateException("Not allowed to anonymous user")

        model.addAttribute("user", authUser)
        messageSource.getMessage("UnsubscribtionWarning.user", emptyArray(), locale).let {
            model.addAttribute("messageContent", it)
            model.addAttribute("messageType", "error")
        }
        return "userAccount/unsubscribe"
    }

    @GetMapping("/userAccount/processUnsubscription")
    fun processUnsubscription(authentication: Authentication, redirectAttributes: RedirectAttributes, locale: Locale):String {
        messageSource.getMessage("useraccount.unsubscribe.success", emptyArray(), locale).let {
            redirectAttributes.addFlashAttribute("message", it)
        }
        val authUser: User = authentication.principal as User
        if(authUser.isAnonymous()) throw IllegalStateException("Not allowed to anonymous user")

        userService.disableUser(authUser)
        return "redirect:/logout"
    }

    @GetMapping("/terms")
    fun terms(model: Model, locale: Locale):String {
        model.addAttribute("termsContent",termsService.getTermsContentByLanguage(locale.language))
        return "terms/terms"
    }

}
