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
import org.elaastic.questions.assignment.AssignmentService
import org.elaastic.questions.controller.MessageBuilder
import org.elaastic.questions.directory.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import javax.transaction.Transactional
import org.springframework.web.bind.annotation.RequestMapping
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Controller
@RequestMapping("/subject")
@Transactional
public class SubjectController(
        @Autowired val assignmentService: AssignmentService,
        @Autowired val messageBuilder: MessageBuilder
){

    @GetMapping(value = ["/{id}", "{id}/show"])
    fun show(authentication: Authentication, model: Model, @PathVariable id: Long): String {
        val user: User = authentication.principal as User
        var subjectTemp:Subject? = null ;
        assignmentService.get(user, id, fetchSequences = true).let {
            model.addAttribute("user", user)
            model.addAttribute("assignment", it)
            subjectTemp = Subject(id=it.id,title=it.title,course = "Cours temporaire",owner = it.owner)
        }


        model.addAttribute("subjectItem",subjectTemp)

        return "/subject/show"
    }

    data class Subject(
            var id: Long? = null,
            @field:NotBlank var title: String? = null,
            var course: String? = null,
            @field:NotNull var owner: User? = null
    )
}
