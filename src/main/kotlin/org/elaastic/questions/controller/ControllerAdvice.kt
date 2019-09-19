package org.elaastic.questions.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletRequest
import org.springframework.web.multipart.MaxUploadSizeExceededException






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


    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun handleMaxSizeException(
            exc: MaxUploadSizeExceededException,
            request: HttpServletRequest,
            response: HttpServletResponse): ModelAndView {

        val modelAndView = ModelAndView("error")

        modelAndView.model["message"] = exc.message
        return modelAndView
    }

    // TODO Handle OptimisticLockingException
}
