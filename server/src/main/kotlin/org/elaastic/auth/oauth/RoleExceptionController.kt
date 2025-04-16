package org.elaastic.auth.oauth

import org.springframework.context.MessageSource
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.*

@Controller
class RoleExceptionController(
    private val messageSource: MessageSource
) {

    @GetMapping("error/oidc_role")
    fun handleRoleException(
        model: Model,
        @RequestParam("message", required = false) message: String?,
        locale: Locale
    ): String {
        model["errorMessage"] = message ?: messageSource.getMessage("oidc.role_error", null, locale)
        return "error/oidc_role"
    }
}