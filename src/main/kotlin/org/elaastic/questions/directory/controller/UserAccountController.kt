package org.elaastic.questions.directory.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

/**
 * @author John Tranier
 */
@Controller
class UserAccountController(
        @Value( "\${elaastic.auth.check_user_email:true}" )
        val checkEmail: Boolean
) {

    @GetMapping("/register")
    fun showSubscribeForm(model: Model) : String {
        model.addAttribute("checkEmail", checkEmail)
        return "/userAccount/showSubscribeForm"
    }

    @GetMapping("/userAccount/edit")
    fun edit(): String {
        // TODO implement
        return "/userAccount/edit"
    }

}
