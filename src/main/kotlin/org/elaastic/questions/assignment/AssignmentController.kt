package org.elaastic.questions.assignment

import org.elaastic.questions.directory.User
import org.elaastic.questions.persistence.pagination.PaginationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import java.util.*
import java.util.stream.Collectors
import java.util.stream.IntStream
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
              @RequestParam("page") page: Int?,
              @RequestParam("size") size: Int?): ModelAndView {
        val user: User = authentication.principal as User

        assignmentService.findAllByOwner(
                user,
                PageRequest.of((page ?: 1) - 1, size ?: 10)
        ).let {
            return ModelAndView(
                    "/assignment/index",
                    mapOf(
                            "user" to user,
                            "assignmentPage" to it,
                            "pagination" to paginationService.buildInfo(
                                    it.totalPages,
                                    page
                            )
                    )

            )
        }

    }

}