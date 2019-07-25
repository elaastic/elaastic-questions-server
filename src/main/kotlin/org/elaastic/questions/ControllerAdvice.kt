package org.elaastic.questions

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ModelAttribute



/**
 * @author John Tranier
 */
@ControllerAdvice
class ControllerAdvice {

    @Value("\${elaastic.questions.version}")
    private lateinit var applicationVersion: String

    @ModelAttribute("applicationVersion")
    fun getApplicationVersion(): String {
        return applicationVersion
    }
}