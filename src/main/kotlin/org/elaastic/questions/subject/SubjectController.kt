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

package org.elaastic.questions.subject

import org.elaastic.questions.assignment.Assignment
import org.elaastic.questions.assignment.AssignmentController
import org.elaastic.questions.assignment.AssignmentService
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
import javax.servlet.http.HttpServletResponse
import javax.transaction.Transactional
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Controller
@RequestMapping("/subject")
@Transactional
public class SubjectController(
        @Autowired val subjectService: SubjectService,
        @Autowired val messageBuilder: MessageBuilder
){

    @GetMapping(value = ["", "/", "/index"])
    fun index(authentication: Authentication,
              model: Model,
              @RequestParam("page") page: Int?,
              @RequestParam("size") size: Int?): String {
        val user: User = authentication.principal as User

        subjectService.findAllByOwner(
                user,
                PageRequest.of((page ?: 1) - 1, size ?: 10, Sort.by(Sort.Direction.DESC, "lastUpdated"))
        ).let {
            model.addAttribute("user", user)
            model.addAttribute("subjectPage", it)
            model.addAttribute(
                    "pagination",
                    PaginationUtil.buildInfo(
                            it.totalPages,
                            page,
                            size
                    )
            )
        }

        return "/subject/index"
    }

    @GetMapping(value = ["/{id}", "{id}/show"])
    fun show(authentication: Authentication, model: Model, @PathVariable id: Long): String {
        val user: User = authentication.principal as User

        subjectService.get(user, id, fetchStatements = true, fetchAssignments = true).let {
            model.addAttribute("user", user)
            model.addAttribute("subject", it)
        }

        return "/subject/show"
    }

    @GetMapping("create")
    fun create(authentication: Authentication, model: Model): String {
        val user: User = authentication.principal as User

        if (!model.containsAttribute("subject")) {
            model.addAttribute("subject", SubjectData(owner = user))
        }
        model.addAttribute("user", user)

        return "/subject/create"
    }

    @PostMapping("save")
    fun save(authentication: Authentication,
             @Valid @ModelAttribute subjectData: SubjectController.SubjectData,
             result: BindingResult,
             model: Model,
             response: HttpServletResponse): String {
        val user: User = authentication.principal as User

        return if (result.hasErrors()) {
            response.status = HttpStatus.BAD_REQUEST.value()
            model.addAttribute("user", user)
            model.addAttribute("subject", subjectData)
            "/subject/create"
        } else {
            val subject = subjectData.toEntity()
            subjectService.save(subject)
            "redirect:/subject/${subject.id}"
        }
    }

    data class SubjectData(
            var id: Long? = null,
            var version: Long? = null,
            @field:NotBlank var title: String? = null,
            var course: String? = null,
            @field:NotNull var owner: User? = null
    ) {
        fun toEntity(): Subject {
            return Subject(
                    title = title!!,
                    owner = owner!!,
                    course = course!!
            ).let {
                it.id = id
                it.version = version
                it
            }
        }
    }

}
