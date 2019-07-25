package org.elaastic.questions

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

/**
 * Controller for the application root page ("/")
 * @author John Tranier
 */
@Controller
class IndexController {

    @GetMapping("/")
    fun index(): String {
        return "/index"
    }
}