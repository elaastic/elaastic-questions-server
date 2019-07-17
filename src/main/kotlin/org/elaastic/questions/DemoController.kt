package org.elaastic.questions

import org.elaastic.questions.directory.Role
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class DemoController() {

    @GetMapping("/demo")
    fun demo(model: Model): String {
        model.addAttribute("ADMIN_ROLE_NAME", Role.RoleId.ADMIN.name)
        model.addAttribute("TEACHER_ROLE_NAME", Role.RoleId.TEACHER.name)
        model.addAttribute("STUDENT_ROLE_NAME", Role.RoleId.STUDENT.name)
        return "demo"
    }
}
