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

package org.elaastic.questions.course;

import org.elaastic.questions.controller.MessageBuilder
import org.elaastic.questions.directory.User
import org.elaastic.questions.persistence.pagination.PaginationUtil
import org.elaastic.questions.subject.Subject
import org.elaastic.questions.subject.SubjectController
import org.elaastic.questions.subject.SubjectService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import javax.servlet.http.HttpServletResponse
import javax.transaction.Transactional;
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Controller
@RequestMapping("/course")
@Transactional
class CourseController(
        @Autowired val courseService: CourseService,
        @Autowired val messageBuilder: MessageBuilder
        /* @Autowired val subjectService: SubjectService */
) {

    @GetMapping(value = ["", "/", "/index"])
    fun index(authentication: Authentication,
              model: Model,
              @RequestParam("page") page: Int?,
              @RequestParam("size") size: Int?): String {

        val user : User = authentication.principal as User

        /*
        val courseNonVide = Course("Course pas vide test", user)
        val subject = Subject("Sujet pour cours non vide", user)
        subject.course = courseNonVide
        subjectService.save(subject)

        courseNonVide.subjects.add(subject)
        courseService.save(courseNonVide)
        */

        courseService.findAllByOwner(user, PageRequest.of((page ?: 1) - 1, size ?: 10, Sort.by(Sort.Direction.DESC, "lastUpdated")))
                .let {
                    model.addAttribute("user", user)
                    model.addAttribute("coursePage", it)
                    model.addAttribute("pagination",
                            PaginationUtil.buildInfo(
                                    it.totalPages,
                                    page,
                                    size
                            )
                    )
                }
        return "/course/index"
    }


    @GetMapping("create")
    fun create(authentication: Authentication, model: Model): String {
        val user: User = authentication.principal as User

        if (!model.containsAttribute("course")) {
            model.addAttribute("course", CourseData(owner = user))
        }
        model.addAttribute("user", user)

        return "/course/create"
    }

    @GetMapping(value = ["/{id}", "{id}/show"])
    fun show(authentication: Authentication, model: Model,
             @PathVariable id: Long): String {

        val user: User = authentication.principal as User
        model.addAttribute("user", user)

        var course: Course = courseService.get(id, fetchSubjects = true)
        model.addAttribute("course", course)

        var subjects: MutableList<Subject> = ArrayList()
        for(subject: Subject in course.subjects){
            if(!subjects.contains(subject)){
                subjects.add(subject)
            }
        }
        model.addAttribute("subjects", subjects)

        return "/course/show"
    }

    @PostMapping("save")
    fun save(authentication: Authentication,
             @Valid @ModelAttribute courseData: CourseData,
             result: BindingResult,
             model: Model,
             response: HttpServletResponse,
             redirectAttributes: RedirectAttributes): String {
        val user: User = authentication.principal as User

        return if (result.hasErrors()) {
            response.status = HttpStatus.BAD_REQUEST.value()
            model.addAttribute("user", user)
            model.addAttribute("course", courseData)
            "/course/create"
        } else {
            val course = courseData.toEntity()
            courseService.save(course)
            "redirect:/course/${course.id}"
        }
    }

    @GetMapping("{id}/delete")
    fun delete(authentication: Authentication,
               @PathVariable id: Long,
               redirectAttributes: RedirectAttributes): String {
        val user: User = authentication.principal as User

        val course = courseService.get(user, id)
        courseService.delete(user, course)

        with(messageBuilder) {
            success(
                redirectAttributes,
                message(
                    "course.deleted.message",
                    message("course.label"),
                    course.title
                )
            )
        }

        return "redirect:/course"
    }

    data class CourseData(
            var id: Long? = null,
            var version: Long? = null,
            @field:NotBlank var title: String? = null,
            @field:NotNull var owner: User? = null
    ) {
        fun toEntity(): Course {
            return Course(
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