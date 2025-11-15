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

package org.elaastic.material.instructional.course

import org.elaastic.common.persistence.pagination.PaginationUtil
import org.elaastic.common.web.MessageBuilder
import org.elaastic.material.instructional.subject.SubjectController
import org.elaastic.material.instructional.subject.SubjectService
import org.elaastic.user.PrincipalUserResolver
import org.elaastic.user.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import javax.servlet.http.HttpServletResponse
import javax.transaction.Transactional
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Controller
@RequestMapping("/course")
@Transactional
class CourseController(
    @Autowired val courseService: CourseService,
    @Autowired val subjectService: SubjectService,
    @Autowired val messageBuilder: MessageBuilder
) {

    @GetMapping(value = ["", "/", "/index"])
    fun index(
        authentication: Authentication,
        model: Model,
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ): String {

        val user = (authentication.principal as PrincipalUserResolver).elaasticUser

        courseService.findAllWithSubjectsByOwner(
            user,
            PageRequest.of((page ?: 1) - 1, size ?: 8, Sort.by(Sort.Direction.DESC, "lastUpdated"))
        ).let {
            model["user"] = user
            model["coursePage"] = it
            model["nbSubjectsWithoutCourse"] = subjectService.countWithoutCourse(user)
            model["pagination"] = PaginationUtil.buildInfo(
                it.totalPages,
                page,
                size
            )
        }

        return "course/index"
    }


    @GetMapping("create")
    fun create(authentication: Authentication, model: Model): String {
        val user = (authentication.principal as PrincipalUserResolver).elaasticUser

        if (!model.containsAttribute("course")) {
            model["course"] = CourseData(owner = user)
        }
        model["user"] = user

        return "course/create"
    }

    @GetMapping(value = ["/{courseId}", "{courseId}/show"])
    fun show(
        authentication: Authentication, model: Model,
        @PathVariable courseId: Long,
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ): String {

        val user = (authentication.principal as PrincipalUserResolver).elaasticUser
        model["user"] = user

        return if (courseId != -1L) {
            val course = courseService.get(courseId, fetchSubjects = true)

            model["course"] = course
            model["subjects"] = course.subjects.toList()

            "course/show"
        } else {
            subjectService.findAllWithoutCourseByOwner(
                user,
                PageRequest.of((page ?: 1) - 1, size ?: 10, Sort.by(Sort.Direction.DESC, "lastUpdated"))
            ).let {
                model["subjectsPage"] = it
                model["pagination"] = PaginationUtil.buildInfo(it.totalPages, page, size)
            }

            "course/show-without-course"
        }
    }

    @PostMapping("{courseId}/update")
    fun update(
        authentication: Authentication,
        @Valid @ModelAttribute courseData: CourseData,
        result: BindingResult,
        model: Model,
        @PathVariable courseId: Long,
        response: HttpServletResponse,
        redirectAttributes: RedirectAttributes
    ): String {
        val user = (authentication.principal as PrincipalUserResolver).elaasticUser

        model["user"] = user

        return if (result.hasErrors()) {
            response.status = HttpStatus.BAD_REQUEST.value()
            model["course"] = courseData

            "redirect:/course/$courseId"
        } else {
            courseService.get(user, courseId).let {
                it.updateFrom(courseData.toEntity())
                courseService.save(it)

                with(messageBuilder) {
                    success(
                        redirectAttributes,
                        message(
                            "course.updated.message",
                            message("course.label"),
                            it.title
                        )
                    )
                }
                model["course"] = it

                "redirect:/course/$courseId"
            }
        }
    }

    @PostMapping("save")
    fun save(
        authentication: Authentication,
        @Valid @ModelAttribute courseData: CourseData,
        result: BindingResult,
        model: Model,
        response: HttpServletResponse,
        redirectAttributes: RedirectAttributes
    ): String {
        val user = (authentication.principal as PrincipalUserResolver).elaasticUser

        return if (result.hasErrors()) {
            response.status = HttpStatus.BAD_REQUEST.value()
            model["user"] = user
            model["course"] = courseData

            "/course/create"
        } else {
            val course = courseData.toEntity()
            courseService.save(course)

            "redirect:/course/${course.id}"
        }
    }

    @GetMapping("firstCourse")
    fun firstCourse(
        authentication: Authentication,
        model: Model,
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ): String {
        val user = (authentication.principal as PrincipalUserResolver).elaasticUser
        val firstCourse = courseService.findFirstCourseByOwner(user) ?: createExampleCourse(user)

        return "redirect:/course/${firstCourse.id}"
    }

    private fun createExampleCourse(user: User): Course =
        courseService.save(CourseData(title = "Example Course", owner = user).toEntity())

    @GetMapping("{courseId}/delete")
    fun delete(
        authentication: Authentication,
        @PathVariable courseId: Long,
        redirectAttributes: RedirectAttributes
    ): String {
        val user = (authentication.principal as PrincipalUserResolver).elaasticUser

        val course = courseService.get(user, courseId)
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

    @GetMapping("{courseId}/addSubject")
    fun addSubject(
        authentication: Authentication,
        model: Model,
        @PathVariable courseId: Long
    ): String {
        val user = (authentication.principal as PrincipalUserResolver).elaasticUser
        val course = courseService.get(user, courseId)

        model["user"] = user
        model["course"] = course
        model["listCourse"] = courseService.findAllByOwner(user)
        model["subjectData"] = SubjectController.SubjectData(owner = user, course = course)

        return "subject/create"
    }

    data class CourseData(
        var id: Long? = null,
        var version: Long? = null,
        @field:NotBlank var title: String? = null,
        @field:NotNull var owner: User? = null
    ) {
        fun toEntity(): Course {
            return Course(title!!, owner!!).also {
                it.id = id
                it.version = version
            }
        }
    }

}