package org.elaastic.questions.assignment

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

/**
 * @author John Tranier
 */
@Controller
@RequestMapping("/assignment")
class AssignmentController {

    @GetMapping(value=arrayOf("", "/", "/index"))
    fun index(): String {
        return "/assignment/index"
    }

}