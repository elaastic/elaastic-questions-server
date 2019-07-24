package org.elaastic.questions.directory

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author John Tranier
 */
@RestController
class UserAccountController {

    @GetMapping("/register")
    fun showSubscribeForm() : String {
        // TODO implement
        return "/register"
    }

    @GetMapping("/userAccount/edit")
    fun edit(): String {
        // TODO implement
        return "/userAccount/edit"
    }
}