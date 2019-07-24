package org.elaastic.questions

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

/**
 * @author John Tranier
 */
@Controller
class IndexController {

    @GetMapping("/")
    fun index(): String {
        return "/index"
    }

    @GetMapping("/home")
    fun home(): String {
        return "/home"
    }
}