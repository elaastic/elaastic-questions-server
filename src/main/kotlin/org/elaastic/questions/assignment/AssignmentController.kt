package org.elaastic.questions.assignment

import org.elaastic.questions.assignment.sequence.SequenceController
import org.elaastic.questions.controller.MessageBuilder
import org.elaastic.questions.directory.User
import org.elaastic.questions.persistence.pagination.PaginationUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import javax.servlet.http.HttpServletResponse
import javax.transaction.Transactional
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull


@Controller
@RequestMapping("/assignment")
@Transactional
class AssignmentController(
        @Autowired val assignmentService: AssignmentService,
        @Autowired val messageBuilder: MessageBuilder
) {

    @GetMapping(value = ["", "/", "/index"])
    fun index(authentication: Authentication,
              model: Model,
              @RequestParam("page") page: Int?,
              @RequestParam("size") size: Int?): String {
        val user: User = authentication.principal as User

        assignmentService.findAllByOwner(
                user,
                PageRequest.of((page ?: 1) - 1, size ?: 10, Sort.by(Sort.Direction.DESC, "lastUpdated"))
        ).let {
            model.addAttribute("user", user)
            model.addAttribute("assignmentPage", it)
            model.addAttribute(
                    "pagination",
                    PaginationUtil.buildInfo(
                            it.totalPages,
                            page
                    )
            )
        }

        return "/assignment/index"
    }

    @GetMapping(value = ["/{id}", "{id}/show"])
    fun show(authentication: Authentication, model: Model, @PathVariable id: Long): String {
        val user: User = authentication.principal as User

        assignmentService.get(user, id, fetchSequences = true).let {
            model.addAttribute("user", user)
            model.addAttribute("assignment", it)
        }

        return "/assignment/show"
    }

    @GetMapping("create")
    fun create(authentication: Authentication, model: Model): String {
        val user: User = authentication.principal as User

        if (!model.containsAttribute("assignment")) {
            model.addAttribute("assignment", AssignmentData(owner = user))
        }
        model.addAttribute("user", user)

        return "/assignment/create"
    }

    @PostMapping("save")
    fun save(authentication: Authentication,
             @Valid @ModelAttribute assignmentData: AssignmentData,
             result: BindingResult,
             model: Model,
             response: HttpServletResponse): String {
        val user: User = authentication.principal as User

        return if (result.hasErrors()) {
            response.status = HttpStatus.BAD_REQUEST.value()
            model.addAttribute("user", user)
            model.addAttribute("assignment", assignmentData)
            "/assignment/create"
        } else {
            val assignment = assignmentData.toEntity()
            assignmentService.save(assignment)
            "redirect:/assignment/${assignment.id}"
        }
    }

    @GetMapping("{id}/edit")
    fun edit(authentication: Authentication,
             model: Model,
             @PathVariable id: Long): String {
        val user: User = authentication.principal as User

        assignmentService.get(user, id).let {
            model.addAttribute("user", user)
            model.addAttribute("assignment", it)
        }

        return "/assignment/edit"
    }

    @PostMapping("{id}/update")
    fun update(authentication: Authentication,
               @Valid @ModelAttribute assignmentData: AssignmentData,
               result: BindingResult,
               model: Model,
               @PathVariable id: Long,
               response: HttpServletResponse,
               redirectAttributes: RedirectAttributes): String {
        val user: User = authentication.principal as User

        return if (result.hasErrors()) {
            response.status = HttpStatus.BAD_REQUEST.value()
            model.addAttribute("user", user)
            model.addAttribute("assignment", assignmentData)
            "/assignment/edit"
        } else {
            assignmentService.get(user, id).let {
                it.updateFrom(assignmentData.toEntity())
                assignmentService.save(it)

                with(messageBuilder) {
                    success(
                            redirectAttributes,
                            message(
                                    "assignment.updated.message",
                                    message("assignment.label"),
                                    it.title
                            )
                    )
                }

                "redirect:/assignment/$id"
            }
        }
    }

    @GetMapping("{id}/delete")
    fun delete(authentication: Authentication,
               @PathVariable id: Long,
               redirectAttributes: RedirectAttributes): String {
        val user: User = authentication.principal as User

        val assignment = assignmentService.get(user, id)
        assignmentService.delete(user, id)

        with(messageBuilder) {
            success(
                    redirectAttributes,
                    message(
                            "assignment.deleted.message",
                            message("assignment.label"),
                            assignment.title
                    )
            )
        }

        return "redirect:/assignment"
    }

    // TODO Duplicate action

    @GetMapping("{id}/addSequence")
    fun addSequence(authentication: Authentication,
                    model: Model,
                    @PathVariable id: Long): String {
        val user: User = authentication.principal as User

        val assignment = assignmentService.get(user, id)
        val nbSequence = assignmentService.countAllSequence(assignment)

        model.addAttribute("user", user)
        model.addAttribute("assignment", assignment)
        model.addAttribute("nbSequence", nbSequence)
        model.addAttribute(
                "statementData",            
                SequenceController.StatementData(
                        Statement.createDefaultStatement(user)
                )
        )

        return "/assignment/sequence/create"
    }

    data class AssignmentData(
            var id: Long? = null,
            var version: Long? = null,
            @field:NotBlank var title: String? = null,
            @field:NotNull var owner: User? = null
    ) {
        fun toEntity(): Assignment {
            return Assignment(
                    title = title!!,
                    owner = owner!!
            ).let {
                it.id = id
                it.version = version
                it
            }
        }
    }
}