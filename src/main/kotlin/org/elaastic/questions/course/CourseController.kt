package org.elaastic.questions.course;

import org.elaastic.questions.directory.User
import org.elaastic.questions.subject.Subject
import org.elaastic.questions.subject.SubjectController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping;
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
        @Autowired val courseService: CourseService
) {


    @GetMapping("create")
    fun create(authentication: Authentication, model: Model): String {
        val user: User = authentication.principal as User

        if (!model.containsAttribute("course")) {
            model.addAttribute("course", CourseData(owner = user))
        }
        model.addAttribute("user", user)

        return "/course/create"
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
            redirectAttributes.addAttribute("activeTab", "questions");
            "redirect:/course/${course.id}"
        }
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