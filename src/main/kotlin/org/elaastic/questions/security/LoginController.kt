package org.elaastic.questions.security

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping


@Controller
class LoginController {

    @GetMapping("/login")
    fun displayLoginForm(): String {
        return "/login"
    }


}
