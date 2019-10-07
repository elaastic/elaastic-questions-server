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


import org.elaastic.questions.directory.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.lang.Exception
import java.util.*
import java.util.logging.Logger

@Controller
class PasswordResetKeyController(
        @Autowired val userService: UserService,
        @Autowired val messageSource: MessageSource
) {

    val logger = Logger.getLogger(PasswordResetKeyController::class.java.name)

    @GetMapping("/userAccount/beginPasswordReset")
    fun doBeginPasswordReset(): String {
        return "/userAccount/beginPasswordReset"
    }


    @PostMapping("/userAccount/processPasswordResetRequest")
    fun doProcessPasswordResetRequest(@RequestParam("email") email:String, model:Model, locale:Locale): String {
        val user = userService.findByEmail(email)
        when(user) {
            null -> {
                model.addAttribute("message",messageSource.getMessage(
                        "passwordReset.email.fail",
                        emptyArray(),
                        locale))
                return "/userAccount/beginPasswordReset"
            }
            else -> {
                userService.generatePasswordResetKeyForUser(user)
                model.addAttribute("message",messageSource.getMessage(
                        "passwordReset.email.success",
                        emptyArray(),
                        locale))
                return "/userAccount/confirmPasswordReset"
            }

        }
    }

    @GetMapping("/userAccount/resetPassword")
    fun doResetPassword(): String {
        return "/userAccount/resetPassword"
    }

    @PostMapping("/userAccount/processResetPassword")
    fun doProcessResetPassword(resetPasswordData: ResetPasswordData, redirectAttributes: RedirectAttributes, locale: Locale): String {
        if (resetPasswordData.password != resetPasswordData.passwordConfirm ) {
            messageSource.getMessage("useraccount.form.password.identical", emptyArray(), locale).let {
                redirectAttributes.addFlashAttribute("message", it)
            }
            redirectAttributes.addAttribute("passwordResetKey", resetPasswordData.passwordResetKey)
            return "redirect:/userAccount/resetPassword"
        }
        val user = userService.findByPasswordResetKeyValue(resetPasswordData.passwordResetKey)!!
        try {
            userService.changePasswordForUser(user, resetPasswordData.password)
            messageSource.getMessage("useraccount.update.success", emptyArray(), locale).let {
                redirectAttributes.addFlashAttribute("message", it)
            }
            return "redirect:/login"
        } catch(e: Exception) {
            messageSource.getMessage("user.plainTextPassword.short", emptyArray(), locale).let {
                redirectAttributes.addFlashAttribute("message", it)
            }
            logger.fine(e.message)
            redirectAttributes.addAttribute("passwordResetKey", resetPasswordData.passwordResetKey)
            return "redirect:/userAccount/resetPassword"
        }

    }

    data class ResetPasswordData(
            val passwordResetKey: String,
            val password: String,
            val passwordConfirm: String
    )


}
