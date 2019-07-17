package org.elaastic

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class DemoController {

    @GetMapping("/demo")
    fun index(): String {
        return "index"
    }
}
