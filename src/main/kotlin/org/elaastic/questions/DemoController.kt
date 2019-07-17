package org.elaastic.questions

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class DemoController() {

    @GetMapping("/demo")
    fun demo(): String {
        return "demo"
    }
}
