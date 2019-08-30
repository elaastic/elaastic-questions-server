package org.elaastic.questions.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ModelAttribute




@ControllerAdvice
class ControllerAdvice {

    @Value("\${elaastic.questions.version}") 
    private lateinit var applicationVersion: String

    @ModelAttribute("applicationVersion")
    fun getApplicationVersion(): String {
        return applicationVersion
    }

    @ModelAttribute("logoutUrl")
    fun getLogoutUrl(): String {
        return "logout"
    }

    // TODO Handle OptimisticLockingException
}