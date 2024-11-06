package org.elaastic.common.web

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@ControllerAdvice("org.elaastic.questions.api")
@RestControllerAdvice
class RestControllerAdvice {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException) =
        ResponseEntity(e.message, HttpStatus.BAD_REQUEST)

}