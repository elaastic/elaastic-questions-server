package org.elaastic.questions

import org.elaastic.questions.directory.User
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView

/**
 * Controller for the homepage of each user
 * It basically delegates to the appropriate controller depending on the user role
 * @author John Tranier
 */
@Controller
class HomeController {

    @GetMapping("/home")
    fun home(authentication: Authentication): ModelAndView {
        val user: User = authentication.principal as User

        return ModelAndView(
                when {
                    user.isLearner() -> "forward:/player/index"
                    else -> "forward:/assignment/index"
                }

        )
    }
}