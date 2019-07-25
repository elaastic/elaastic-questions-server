package org.elaastic.questions.assignment

import org.elaastic.questions.directory.User
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import javax.transaction.Transactional

/**
 * @author John Tranier
 */
@Controller
@RequestMapping("/assignment")
@Transactional
class AssignmentController {


    @GetMapping(value=arrayOf("", "/", "/index"))
    fun index(authentication: Authentication): ModelAndView {
        val user: User = authentication.principal as User
        // TODO Paginate
        // TODO Get the data through the service

        return ModelAndView(
                "/assignment/index",
                mapOf(
                        "assignmentInstanceList" to listOf<Assignment>(
                                Assignment(title = "Titre", owner = user, globalId = "123")
                        )
                )

        )
    }

}