package org.elaastic.questions.assignment

import org.elaastic.questions.directory.User
import org.springframework.beans.factory.annotation.Autowired
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
class AssignmentController(
        @Autowired val assignmentService: AssignmentService
) {


    @GetMapping(value=arrayOf("", "/", "/index"))
    fun index(authentication: Authentication): ModelAndView {
        val user: User = authentication.principal as User
        // TODO Paginate

        return ModelAndView(
                "/assignment/index",
                mapOf(
                        "user" to user,
                        "assignmentInstanceList" to assignmentService.findAllByOwner(user)
                )

        )
    }

}