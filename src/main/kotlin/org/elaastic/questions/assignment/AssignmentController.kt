/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.elaastic.questions.assignment

import org.elaastic.common.web.MessageBuilder
import org.elaastic.questions.directory.User
import org.elaastic.common.persistence.pagination.PaginationUtil
import org.elaastic.questions.subject.Subject
import org.elaastic.questions.subject.SubjectService
import org.elaastic.questions.subject.statement.Statement
import org.elaastic.questions.subject.statement.StatementController
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
    @Autowired val messageBuilder: MessageBuilder,
    @Autowired val subjectService: SubjectService
) {

    @GetMapping(value = ["", "/", "/index"])
    fun index(
        authentication: Authentication,
        model: Model,
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ): String {
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
                    page,
                    size
                )
            )
        }

        return "assignment/index"
    }

    @GetMapping(value = ["/{id}", "{id}/show"])
    fun show(authentication: Authentication, model: Model, @PathVariable id: Long): String {
        val user: User = authentication.principal as User

        assignmentService.get(user, id, fetchSequences = true).let {
            model.addAttribute("user", user)
            model.addAttribute("assignment", it)
        }

        return "assignment/show"
    }

    @PostMapping("save")
    fun save(
        authentication: Authentication,
        @Valid @ModelAttribute assignmentData: AssignmentData,
        result: BindingResult,
        model: Model,
        response: HttpServletResponse,
        redirectAttributes: RedirectAttributes
    ): String {
        val user: User = authentication.principal as User
        val subject: Subject = subjectService.get(assignmentData.subject.id!!)

        return if (result.hasErrors()) {
            response.status = HttpStatus.BAD_REQUEST.value()
            model.addAttribute("user", user)
            model.addAttribute("assignment", assignmentData)
            redirectAttributes.addAttribute("activeTab", "assignments")
            "/subject/${subject.id}/addAssignment"
        } else {
            val assignment = assignmentData.toEntity()
            assignmentService.save(assignment)
            redirectAttributes.addAttribute("activeTab", "assignments")
            "/subject/${subject.id}"
        }
    }

    @GetMapping("{id}/edit")
    fun edit(
        authentication: Authentication,
        model: Model,
        @PathVariable id: Long
    ): String {
        val user: User = authentication.principal as User

        assignmentService.get(user, id).let {
            model.addAttribute("user", user)
            model.addAttribute("assignment", it)
            model.addAttribute("subject", it.subject)
        }

        return "assignment/edit"
    }

    @PostMapping("{id}/update")
    fun update(
        authentication: Authentication,
        @Valid @ModelAttribute assignmentData: AssignmentData,
        result: BindingResult,
        model: Model,
        @PathVariable id: Long,
        response: HttpServletResponse,
        redirectAttributes: RedirectAttributes
    ): String {

        val user: User = authentication.principal as User
        val assignment: Assignment = assignmentService.get(id)

        return if (result.hasErrors()) {

            model.addAttribute("user", user)
            model.addAttribute("nbAssignments", assignment.subject!!.assignments.size)
            if (!model.containsAttribute("assignment")) {
                model.addAttribute(
                    "assignment",
                    AssignmentData(owner = user, subject = assignment.subject!!, revisionMode = assignment.revisionMode)
                )
            }
            return "assignment/create"

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
                redirectAttributes.addAttribute("activeTab", "assignments")
                "redirect:/subject/${assignment.subject!!.id}"
            }
        }
    }

    @GetMapping("{id}/delete")
    fun delete(
        authentication: Authentication,
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes
    ): String {
        val user: User = authentication.principal as User

        val assignment = assignmentService.get(user, id)
        subjectService.removeAssignment(user, assignment)

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
        redirectAttributes.addAttribute("activeTab", "assignments")
        return "redirect:/subject/${assignment.subject!!.id}"
    }


    @GetMapping("{id}/addSequence")
    fun addSequence(
        authentication: Authentication,
        model: Model,
        @PathVariable id: Long
    ): String {
        val user: User = authentication.principal as User

        val assignment = assignmentService.get(user, id)
        val nbSequence = assignmentService.countAllSequence(assignment)

        model.addAttribute("user", user)
        model.addAttribute("assignment", assignment)
        model.addAttribute("nbSequence", nbSequence)
        model.addAttribute(
            "statementData",
            StatementController.StatementData(
                Statement.createDefaultStatement(user)
            )
        )

        return "assignment/sequence/create"
    }

    @GetMapping("{id}/up")
    fun up(
        authentication: Authentication,
        subjectId: Long,
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes
    ): String {
        val user: User = authentication.principal as User

        val subject = subjectService.get(user, subjectId, true)
        subjectService.moveUpAssignment(subject, id)
        redirectAttributes.addAttribute("activeTab", "assignments")
        return "redirect:/subject/$subjectId#assignment_${id}"
    }

    @GetMapping("{id}/down")
    fun down(
        authentication: Authentication,
        subjectId: Long,
        @PathVariable id: Long,
        redirectAttributes: RedirectAttributes
    ): String {
        val user: User = authentication.principal as User

        val subject = subjectService.get(user, subjectId, true)
        subjectService.moveDownAssignment(subject, id)
        redirectAttributes.addAttribute("activeTab", "assignments")
        return "redirect:/subject/$subjectId#assignment_${id}"
    }


    data class AssignmentData(
        var id: Long? = null,
        var version: Long? = null,
        @field:NotBlank var title: String? = null,
        @field:NotNull var owner: User? = null,
        var subject: Subject,
        var audience: String = "",
        var description: String = "",
        var scholarYear: String = "",
        var acceptAnonymousUsers: Boolean = false,
        var revisionMode: RevisionMode = RevisionMode.NotAtAll
    ) {
        fun toEntity(): Assignment {
            return Assignment(
                title = title!!,
                owner = owner!!,
                subject = subject,
                scholarYear = scholarYear,
                audience = audience,
                acceptAnonymousUsers = acceptAnonymousUsers,
                revisionMode = revisionMode,
            ).let {
                it.id = id
                it.version = version
                it.audience = audience
                it.description = description
                it
            }
        }
    }
}
