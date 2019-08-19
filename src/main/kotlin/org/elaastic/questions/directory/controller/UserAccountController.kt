package org.elaastic.questions.directory.controller

import org.elaastic.questions.directory.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.util.*


@Controller
class UserAccountController(
        @Value("\${elaastic.auth.check_user_email:true}")
        val checkEmail: Boolean,
        @Autowired val userService: UserService,
        @Autowired val messageSource: MessageSource
) {

    @GetMapping("/register")
    fun showSubscribeForm(model: Model): String {
        model.addAttribute("checkEmail", checkEmail)
        return "/userAccount/showSubscribeForm"
    }

    @GetMapping("/userAccount/edit")
    fun edit(): String {
        // TODO implement
        return "/userAccount/edit"
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
