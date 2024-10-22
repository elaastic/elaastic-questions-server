package org.elaastic.questions.moderation

import org.elaastic.questions.directory.User
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/report-manager")
class ReportManagerController {

    @GetMapping(*["/", ""])
    fun getAllReport(
        authentication: Authentication,
        model: Model,
    ): String {
        val user = authentication.principal as User

        model["user"] = user

        return "moderation/report-manager"
    }
}