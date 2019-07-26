package org.elaastic.questions.assignment

import org.elaastic.questions.directory.User
import org.elaastic.questions.persistence.pagination.PaginationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import javax.transaction.Transactional

/**
 * @author John Tranier
 */
@Controller
@RequestMapping("/assignment")
@Transactional
class AssignmentController(
        @Autowired val assignmentService: AssignmentService,
        @Autowired val paginationService: PaginationService
) {

    @GetMapping(value = arrayOf("", "/", "/index"))
    fun index(authentication: Authentication,
              model: Model,
              @RequestParam("page") page: Int?,
              @RequestParam("size") size: Int?): String {
        val user: User = authentication.principal as User

        assignmentService.findAllByOwner(
                user,
                PageRequest.of((page ?: 1) - 1, size ?: 10)
        ).let {
            model.addAttribute("user", user)
            model.addAttribute("assignmentPage", it)
            model.addAttribute(
                    "pagination",
                    paginationService.buildInfo(
                            it.totalPages,
                            page
                    )
            )
        }

        return "/assignment/index"
    }

    @GetMapping("show/{id}")
    fun show(authentication: Authentication, model: Model, @PathVariable id: Long): String {
        val user: User = authentication.principal as User

        assignmentService.get(id, fetchSequences = true).let {
            model.addAttribute("user", user)
            model.addAttribute("assignment", it)
        }

        return "/assignment/show"
    }

}