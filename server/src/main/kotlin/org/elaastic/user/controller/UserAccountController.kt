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

package org.elaastic.user.controller


import org.elaastic.common.onboarding.OnboardingChapter
import org.elaastic.user.*
import org.elaastic.user.controller.command.PasswordData
import org.elaastic.user.controller.command.UserData
import org.elaastic.user.legal.TermsService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.util.*
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid


private const val NOT_ALLOWED_TO_ANONYMOUS_USER = "Not allowed to anonymous user"

private const val NOT_ALLOWED_TO_OIDC_USER =
    "Oidc user cannot update his profile, use the OIDC provider console to do so."

@Controller
class UserAccountController(
    @Value("\${elaastic.auth.check_user_email:true}") val checkEmail: Boolean,
    @Value("\${oauth2.client.provider.elaastic-keycloak.account-console-url}") private val accountConsoleURL: String,
    @Autowired val userService: UserService,
    @Autowired val roleService: RoleService,
    @Autowired val termsService: TermsService,
    @Autowired val messageSource: MessageSource
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/register")
    fun showSubscribeForm(model: Model): String {
        model["checkEmail"] = checkEmail
        return "userAccount/showSubscribeForm"
    }

    @GetMapping("/userAccount/edit")
    fun edit(authentication: Authentication, model: Model): String {
        val user = (authentication.principal as PrincipalUserResolver).elaasticUser
        check(!user.isAnonymous()) { NOT_ALLOWED_TO_ANONYMOUS_USER }

        val userToUpdate = userService.get(user.id!!)!!
        model["userData"] = UserData(userToUpdate, userHasGivenConsent = true)
        model["user"] = userToUpdate
        model["source"] = userToUpdate.getSource()
        model["accountConsoleURL"] = accountConsoleURL

        return "userAccount/edit"
    }


    @PostMapping("/userAccount/update")
    fun update(
        authentication: Authentication,
        @Valid @ModelAttribute userData: UserData,
        result: BindingResult,
        model: Model,
        response: HttpServletResponse,
        redirectAttributes: RedirectAttributes,
        locale: Locale
    ): String {
        val authUser = (authentication.principal as PrincipalUserResolver).elaasticUser
        check(!authUser.isAnonymous()) { NOT_ALLOWED_TO_ANONYMOUS_USER }
        check(authUser.getSource() != UserSource.OIDC) { NOT_ALLOWED_TO_OIDC_USER }

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
            model["user"] = authUser
            model["userData"] = userData
            model["source"] = authUser.getSource()
            model["accountConsoleURL"] = accountConsoleURL

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
        val user = (authentication.principal as PrincipalUserResolver).elaasticUser
        check(!user.isAnonymous()) { NOT_ALLOWED_TO_ANONYMOUS_USER }
        check(user.getSource() != UserSource.OIDC) { NOT_ALLOWED_TO_OIDC_USER }

        model["passwordData"] = PasswordData(user)
        model["user"] = user

        return "userAccount/editPassword"
    }

    @PostMapping("/userAccount/updatePassword")
    fun updatePassword(
        authentication: Authentication,
        @Valid @ModelAttribute passwordData: PasswordData,
        result: BindingResult,
        model: Model,
        response: HttpServletResponse,
        redirectAttributes: RedirectAttributes,
        locale: Locale
    ): String {
        val authUser = (authentication.principal as PrincipalUserResolver).elaasticUser
        check(!authUser.isAnonymous()) { NOT_ALLOWED_TO_ANONYMOUS_USER }
        check(authUser.getSource() != UserSource.OIDC) { NOT_ALLOWED_TO_OIDC_USER }

        if (!result.hasErrors()) {
            val updatedUser = userService.get(authUser, passwordData.id!!)
            try {
                userService.changePasswordForUserWithCurrentPasswordChecking(
                    updatedUser, passwordData.password!!, passwordData.password1!!
                )
            } catch (e: SecurityException) {
                passwordData.catchSecurityException(e, result)
            }
        }
        return if (result.hasErrors()) {
            response.status = HttpStatus.BAD_REQUEST.value()
            model["user"] = authUser

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
    fun updateOnboardingChapter(authentication: Authentication, @PathVariable chapterToUpdate: String) {
        val user = (authentication.principal as PrincipalUserResolver).elaasticUser
        userService.updateOnboardingChapter(OnboardingChapter.from(chapterToUpdate), user)
    }

    @ResponseBody
    @GetMapping("/userAccount/getOnboardingChapter")
    fun getOnboardingChapter(authentication: Authentication): String? {
        val user = (authentication.principal as PrincipalUserResolver).elaasticUser
        return userService.getOnboardingState(user.id).toString()
    }

    @GetMapping("/userAccount/activate")
    fun doEnableUser(
        @RequestParam("actKey") activationKey: String,
        redirectAttributes: RedirectAttributes,
        locale: Locale
    ): String {
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
    fun unsubscribe(authentication: Authentication, model: Model, locale: Locale): String {
        val authUser = (authentication.principal as PrincipalUserResolver).elaasticUser
        check(!authUser.isAnonymous()) { NOT_ALLOWED_TO_ANONYMOUS_USER }

        model["user"] = authUser
        messageSource.getMessage("UnsubscribtionWarning.user", emptyArray(), locale).let {
            model["messageContent"] = it
            model["messageType"] = "error"
        }
        return "userAccount/unsubscribe"
    }

    @GetMapping("/userAccount/processUnsubscription")
    fun processUnsubscription(
        authentication: Authentication,
        redirectAttributes: RedirectAttributes,
        locale: Locale
    ): String {
        val authUser = (authentication.principal as PrincipalUserResolver).elaasticUser
        val authUser: User = authentication.principal as User

        check(!authUser.isAnonymous()) { NOT_ALLOWED_TO_ANONYMOUS_USER }

        userService.disableUser(authUser)
        messageSource.getMessage("useraccount.unsubscribe.success", emptyArray(), locale).let {
            redirectAttributes.addFlashAttribute("message", it)
        }

        return "redirect:/logout"
    }

    @GetMapping("/terms")
    fun terms(model: Model, locale: Locale): String {
        model["termsContent"] = termsService.getTermsContentByLanguage(locale.language)

        return "terms/terms"
    }

}
