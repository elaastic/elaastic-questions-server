package org.elaastic.questions.player

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping


@Controller
@RequestMapping("/player")
class PlayerController {

    @GetMapping(value = ["", "/", "/index"])
    fun index(): String {
        return "/player/index"
    }
}