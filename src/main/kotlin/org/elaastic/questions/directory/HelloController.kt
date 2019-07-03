package org.elaastic.questions.directory

import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

/**
 * @author John Tranier
 */
@Controller
class HelloController {

    @GetMapping("/")
    fun hello(model: Model): String {
        model.addAttribute("title", "Hello World")
        return "blog"
    }
}